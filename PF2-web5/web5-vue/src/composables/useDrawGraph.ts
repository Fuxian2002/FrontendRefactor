import { ref, reactive, onMounted, watch } from 'vue';
import * as joint from 'jointjs';
import $ from 'jquery';
import { useEditorStore } from '../store/editor';
import { useProjectStore } from '../store/project';
import { storeToRefs } from 'pinia';
import { Project } from '../entity/Project';
import { Machine } from '../entity/Machine';
import { ProblemDomain } from '../entity/ProblemDomain';
import { Requirement } from '../entity/Requirement';
import { Interface } from '../entity/Interface';
import { Reference } from '../entity/Reference';
import { Constraint } from '../entity/Constraint';
import { CtrlNode } from '../entity/CtrlNode';
import { Line } from '../entity/Line';
import { Node } from '../entity/Node';
import { ScenarioGraph } from '../entity/ScenarioGraph';
import { SubProblemDiagram } from '../entity/SubProblemDiagram';
import { Phenomenon } from '../entity/Phenomenon';
import { getPhenomenon } from '../utils/projectLogic';

// Define EndElement (for Control Nodes)
const EndElement = joint.dia.Element.define('EndElement', {
  attrs: {
    body: {
      strokeWidth: 1,
      stroke: '#000000',
      fill: 'rgb(240,255,255)'
    },
    c1: {
      strokeWidth: 4,
      stroke: '#000000',
      fill: 'black',
    },
  }
}, {
  markup: [{
    tagName: 'circle',
    selector: 'body'
  }, {
    tagName: 'circle',
    selector: 'c1'
  }]
});

// Define CustomTextElement (Machine)
const MachineElement = joint.dia.Element.define('examples.CustomTextElement', {
  attrs: {
    label: {
      textAnchor: 'middle',
      textVerticalAnchor: 'middle',
      fontSize: 25,
    },
    r: {
      strokeWidth: 1,
      stroke: '#000000',
      fill: 'rgb(240,255,255)',
    },
    r1: {
      strokeWidth: 1,
      stroke: '#000000',
      fill: 'rgb(240,255,255)',
    },
    r2: {
      strokeWidth: 1,
      stroke: '#000000',
      fill: 'rgb(240,255,255)',
    },
  }
}, {
  markup: [{
    tagName: 'rect',
    selector: 'r'
  }, {
    tagName: 'rect',
    selector: 'r1'
  }, {
    tagName: 'rect',
    selector: 'r2'
  }, {
    tagName: 'text',
    selector: 'label'
  }]
});

// Define DesignDomain (ProblemDomain)
const GivenElement = joint.dia.Element.define('DesignDomain', {
  attrs: {
    label: {
      textAnchor: 'middle',
      textVerticalAnchor: 'middle',
      fontSize: 25,
    },
    r: {
      strokeWidth: 1,
      stroke: '#000000',
      fill: 'rgb(240,255,255)',
    },
    r1: {
      strokeWidth: 1,
      stroke: '#000000',
      fill: 'rgb(240,255,255)',
    },
  }
}, {
  markup: [{
    tagName: 'rect',
    selector: 'r'
  }, {
    tagName: 'rect',
    selector: 'r1'
  }, {
    tagName: 'text',
    selector: 'label'
  }]
});

export function useDrawGraph() {
  const editorStore = useEditorStore();
  const projectStore = useProjectStore();
  const { project } = storeToRefs(projectStore);
  const graphs = ref<joint.dia.Graph[]>([]);
  const papers = ref<joint.dia.Paper[]>([]);
  // const project = ref<Project>(new Project()); // Removed local ref

  // WebSocket State
  const ws = ref<WebSocket | null>(null);
  const messageId = ref(0);
  const projectAddress = ref('');
  const version = ref<string | undefined>(undefined);
  
  // State for linking
  const linkSource = ref<any>(undefined);
  const linkTarget = ref<any>(undefined);

  // Watch for tool changes to clear selection state
  watch(() => editorStore.tool, (newTool) => {
    if (!newTool) {
      if (linkSource.value) {
        // Reset stroke color (default to black)
        linkSource.value.attr('body/stroke', 'black');
        linkSource.value = undefined;
      }
      if (linkTarget.value) {
        linkTarget.value.attr('body/stroke', 'black');
        linkTarget.value = undefined;
      }
    }
  });

  const clickedToolView = ref<any>(null);
  const clickedSceElement = ref<any>(null);
  const clickedPaper = ref<any>(null);
  
  // Panning State
  const isPanning = ref(false);
  const hasPanned = ref(false);
  const dragStartPosition = ref({ x: 0, y: 0 });
  
  // Editing State
  const editingMachine = ref<Machine | null>(null);
  const editingDomain = ref<ProblemDomain | null>(null);
  const editingInterface = ref<Interface | null>(null);
  const editingReference = ref<Reference | null>(null);
  const editingConstraint = ref<Constraint | null>(null);
  const editingRequirement = ref<Requirement | null>(null);

  
  // Counters
  let problemdomain_no = 1;
  let requirement_no = 1;
  let interface_no = 1;

  // Node Lists (for tracking elements)
  const startNodeList: any[] = [];
  const endNodeList: any[] = [];
  const decisionNodeList: any[] = [];
  const mergeNodeList: any[] = [];
  const branchNodeList: any[] = [];
  const delayNodeList: any[] = [];

  const initPapers = () => {
    graphs.value = [];
    papers.value = [];
    
    // Initialize project structure if needed
    if (!project.value) {
      projectStore.setProject(new Project());
      // Initialize with default structure
      if (project.value instanceof Project) {
         project.value.initProject(new Project());
      }
    } else if (!project.value.contextDiagram && project.value instanceof Project) {
      project.value.initProject(new Project());
    }

    const tabNames = ['Context Diagram', 'Problem Diagram'];
    for (let i = 0; i < 2; i++) {
      const graph = new joint.dia.Graph();
      graphs.value[i] = graph;
      
      const containerId = '#content' + (i + 1);
      const el = $(containerId);
      
      if (el.length === 0) continue; // Skip if container not found
      el.empty(); // Clear existing content to prevent duplicate papers

      let width = el.width() || 1200;
      let height = el.height() || 800;
      
      const paper = new joint.dia.Paper({
        el: el,
        width: width,
        height: height,
        model: graph,
        clickThreshold: 1,
        gridSize: 10,
        drawGrid: true,
        interactive: true,
        background: {
          color: 'rgb(240,255,255)'
        }
      });
      papers.value[i] = paper;
      attachPaperEvents(paper, graph, i);

      // Add Drag and Drop support
      const domEl = el[0];
      if (domEl) {
        domEl.ondragover = (e) => {
          e.preventDefault();
        };
        domEl.ondrop = (e) => {
          e.preventDefault();
          const tool = e.dataTransfer?.getData('tool');
          if (tool) {
            const point = paper.clientToLocalPoint({ x: e.clientX, y: e.clientY });
            drawElement(graph, point.x, point.y, paper.id as string, tool);
          }
        };
      }
    }

    // Initialize Scenario Graphs
    if (project.value && project.value.scenarioGraphList) {
      project.value.scenarioGraphList.forEach(sg => {
        drawNewSenarioGraph(sg);
      });
    }

    // Initialize SubProblemDiagrams
    if (project.value) {
      drawNewSpd(project.value);
      if (project.value.contextDiagram) {
        drawContextDiagram1(project.value.contextDiagram);
      }
      if (project.value.problemDiagram) {
        drawProblemDiagram1(project.value.problemDiagram);
      }
    }
  };


  const attachPaperEvents = (paper: joint.dia.Paper, graph: joint.dia.Graph, index: number) => {
    // Zooming
    paper.on('blank:mousewheel', (event, x, y, delta) => {
        event.preventDefault();
        const scale = paper.scale();
        paper.scale(scale.sx + (delta * 0.01), scale.sy + (delta * 0.01));
    });

    // Panning - Start
    paper.on('blank:pointerdown', (evt: any, x: number, y: number) => {
      isPanning.value = true;
      hasPanned.value = false;
      dragStartPosition.value = { x: evt.clientX, y: evt.clientY };
      clickedPaper.value = paper;
    });

    // Panning - Move
    paper.on('blank:pointermove', (evt: any, x: number, y: number) => {
      if (!isPanning.value) return;
      
      const dx = evt.clientX - dragStartPosition.value.x;
      const dy = evt.clientY - dragStartPosition.value.y;
      
      if (Math.abs(dx) > 2 || Math.abs(dy) > 2) {
           hasPanned.value = true;
      }

      const currentTranslate = paper.translate();
      paper.translate(currentTranslate.tx + dx, currentTranslate.ty + dy);
      
      dragStartPosition.value = { x: evt.clientX, y: evt.clientY };
    });

    // Panning - End
    paper.on('blank:pointerup', (evt: any) => {
        isPanning.value = false;
    });

    // Track clicked paper (redundant but kept for other logic if needed)
    paper.on('cell:pointerdown', () => {
      clickedPaper.value = paper;
    });

    // Blank click - Draw new element (Only if not panned)
    paper.on('blank:pointerclick', (evt, x, y) => {
        if (hasPanned.value) {
            hasPanned.value = false;
            return;
        }
        drawElement(graph, x, y, paper.id as string);
    });

    // Element click - Select or Link
    paper.on('element:pointerclick', (elementView, evt, x, y) => {
        // Highlight selection for deletion
        if (clickedSceElement.value) {
            clickedSceElement.value.attr('body/stroke', 'black'); // Reset previous
            clickedSceElement.value.attr('body/strokeWidth', 1);
        }
        clickedSceElement.value = elementView.model;
        clickedSceElement.value.attr('body/stroke', '#1890ff'); // Highlight blue
        clickedSceElement.value.attr('body/strokeWidth', 3);
        clickedPaper.value = paper;

        // Clear link selection if any
        if (clickedToolView.value) {
            clickedToolView.value.hideTools();
            clickedToolView.value = null;
        }

        handleElementClick(elementView, graph, paper);
    });
    
    // Cell click - Delete or Select
    paper.on('cell:pointerclick', (cellView, evt, x, y) => {
        // Handle deletion preparation or selection highlighting here
        // For now, just a placeholder
    });

    paper.on('blank:contextmenu', (evt, x, y) => {
        evt.preventDefault();
        // Clear tool
        editorStore.clearTool();
        // Clear link source/target if any
        if (linkSource.value) {
             linkSource.value.attr('body/stroke', 'black');
             linkSource.value.attr('body/strokeWidth', 1);
             linkSource.value = undefined;
        }
        if (linkTarget.value) {
             linkTarget.value.attr('body/stroke', 'black');
             linkTarget.value.attr('body/strokeWidth', 1);
             linkTarget.value = undefined;
        }
    });

    paper.on('link:pointerclick', (linkView) => {
      if (clickedToolView.value) {
        clickedToolView.value.hideTools();
      }
      if (clickedSceElement.value) {
        clickedSceElement.value.attr('body/stroke', 'black');
        clickedSceElement.value = null;
      }
      clickedToolView.value = linkView;
      linkView.showTools();
    });
  };

  // --- WebSocket Functions ---


  const getMaxCtrlNodeNo = (sgName: string, type: string) => {
    const sgList = project.value.scenarioGraphList;
    let max = 0;
    if (sgList) {
        for (let sg of sgList) {
            if (sg.title === sgName) {
                if (sg.ctrlNodeList) {
                    for (let node of sg.ctrlNodeList) {
                        if (node.node_type === type && node.node_no > max) {
                            max = node.node_no;
                        }
                    }
                }
                break;
            }
        }
    }
    return max;
  };

  const drawElement = (graph: joint.dia.Graph, x: number, y: number, paperId: string, toolOverride?: string) => {
    const tool = toolOverride || editorStore.tool;
    if (!tool) return;

    if (tool === 'Domain') {
      drawProblemDomain(x, y, graph);
      editorStore.clearTool();
    } else if (tool === 'Machine') {
      drawMachine(x, y, graph);
      editorStore.clearTool();
    } else if (tool === 'Requirement') {
      drawRequirement(x, y, graph);
      editorStore.clearTool();
    } else if (['Start', 'End', 'Decision', 'Merge', 'Branch', 'Delay'].includes(tool)) {
      const sgName = paperId.substring(0, paperId.length - 1);
      
      // Determine if we need to create a pair of nodes
      const isPair = ['Start', 'End', 'Branch'].includes(tool);
      
      if (isPair) {
          // Create Pair Logic
          const nextNo1 = getMaxCtrlNodeNo(sgName, tool) + 1;
          const ctrlNode1 = {
            node_type: tool,
            node_no: nextNo1,
            node_text: tool,
            node_x: x,
            node_y: y,
            delay_type: '',
          } as CtrlNode;

          const nextNo2 = nextNo1 + 1; // Assuming sequential ID for pair
          
          // Original logic uses x - 250 for the clone. 
          // We apply logic to keep it on screen: if dropped on right (>300), clone to left. Else clone to right.
          // This matches the original distance (250) but adds safety.
          const cloneX = x > 300 ? x - 250 : x + 250;

          const ctrlNode2 = {
            node_type: tool,
            node_no: nextNo2,
            node_text: tool,
            node_x: cloneX,
            node_y: y,
            delay_type: '',
          } as CtrlNode;

          const el1 = drawCtrlNode(paperId, ctrlNode1, graph);
          const el2 = drawCtrlNode(paperId, ctrlNode2, graph);
          
          // Link them as partners in userData
          if (el1 && el2) {
              const u1 = el1.get('userData') || ctrlNode1;
              const u2 = el2.get('userData') || ctrlNode2;
              el1.set('userData', { ...u1, partner_no: ctrlNode2.node_no, partner_type: ctrlNode2.node_type });
              el2.set('userData', { ...u2, partner_no: ctrlNode1.node_no, partner_type: ctrlNode1.node_type });
          }

          saveCtrlNode(sgName, ctrlNode1);
          saveCtrlNode(sgName, ctrlNode2);
      } else {
          // Single Node Logic
          let nextNo = getMaxCtrlNodeNo(sgName, tool) + 1;
          let ctrlNode = {
            node_type: tool,
            node_no: nextNo,
            node_text: tool,
            node_x: x,
            node_y: y,
            delay_type: '',
          } as CtrlNode;
          
          drawCtrlNode(paperId, ctrlNode, graph);
          saveCtrlNode(sgName, ctrlNode);
      }

      editorStore.clearTool();
    } else if (['BehInt', 'ConnInt', 'ExpInt'].includes(tool)) {
        const sgName = paperId.substring(0, paperId.length - 1);
        const sgList = project.value.scenarioGraphList;
        const sg = sgList?.find(s => s.title === sgName);
        
        if (sg) {
            // Find a phenomenon that matches the tool type but is NOT yet in the graph
            // Heuristic: Check Requirement/Context/Problem Diagram for available phenomena
            let candidatePhe: Phenomenon | null = null;
            
            // Collect all used phenomenon numbers in this SG
            const usedNos = new Set<number>();
            sg.intNodeList?.forEach(n => usedNos.add(n.node_no));
            
            // Search in Context Diagram Interfaces (for BehInt/ConnInt)
            if (tool === 'BehInt' || tool === 'ConnInt') {
                project.value.contextDiagram?.interfaceList?.forEach(iface => {
                    iface.phenomenonList?.forEach(p => {
                        if (!usedNos.has(p.phenomenon_no)) {
                            // Simple type check: usually phenomena don't have explicit types stored in them compatible with node_type directly
                            // But we can assume if it's missing, it's a candidate.
                            // Ideally we check p.phenomenon_type but it might differ string-wise.
                            if (!candidatePhe) candidatePhe = p;
                        }
                    });
                });
            }
            
            // Search in Problem Diagram Constraints/References (for ExpInt)
            if (tool === 'ExpInt') {
                project.value.problemDiagram?.constraintList?.forEach(c => {
                    c.phenomenonList?.forEach(p => {
                        if (!usedNos.has(p.phenomenon_no)) {
                            if (!candidatePhe) candidatePhe = p;
                        }
                    });
                });
            }
            
            // Create Node
            const node = new Node();
            node.node_type = tool;
            node.node_x = x;
            node.node_y = y;
            
            if (candidatePhe) {
                node.node_no = candidatePhe.phenomenon_no;
                node.pre_condition = candidatePhe;
            } else {
                // Fallback: Generate a new unique ID (max + 1)
                let max = 0;
                sg.intNodeList?.forEach(n => max = Math.max(max, n.node_no));
                node.node_no = max + 1;
            }
            
            drawIntNode(paperId, node, graph);
            saveInt(sgName, node);
        }
        editorStore.clearTool();
    }
  };

  // Helper to find partner node for synchronization
  const getPartnerNode = (node: Node, graph: joint.dia.Graph): Node | null => {
      // 1. Try to find partner via userData (for Control Nodes created as pairs)
      const elements = graph.getElements();
      const element = elements.find(el => {
          const userData = el.get('userData');
          return userData && userData.node_type === node.node_type && userData.node_no === node.node_no;
      });
      
      if (element) {
          const userData = element.get('userData');
          if (userData && userData.partner_no && userData.partner_type) {
               // Construct partner node object
               // We need to find the actual partner element to get its full data/position if needed, 
               // but for line creation we mainly need type and no.
               // Let's return a partial Node with correct type/no.
               // Or better, find the partner element and return its userData.
               const partnerEl = elements.find(el => {
                   const ud = el.get('userData');
                   return ud && ud.node_type === userData.partner_type && ud.node_no === userData.partner_no;
               });
               if (partnerEl) {
                   return partnerEl.get('userData') as Node;
               }
          }
      }
      return null;
  };

  const handleElementClick = (elementView: joint.dia.ElementView, graph: joint.dia.Graph, paper: joint.dia.Paper) => {
      // Logic for linking
      const tool = editorStore.tool;
      
      const toolColors: Record<string, string> = {
        'BehOrder': 'blue',
        'BehEnable': 'red',
        'Synchrony': 'green',
        'ExpOrder': 'orange',
        'ExpEnable': 'purple',
        'Interface': 'black',
        'Reference': 'black',
        'Constraint': 'black'
      };

      if (['Interface', 'Reference', 'Constraint', 'BehOrder', 'BehEnable', 'Synchrony', 'ExpOrder', 'ExpEnable'].includes(tool || '')) {
          const color = toolColors[tool!] || 'red';

          if (!linkSource.value) {
              linkSource.value = elementView.model;
              // Visual feedback with specific color
              elementView.model.attr('body/stroke', color); 
              elementView.model.attr('body/strokeWidth', 3);
          } else if (!linkTarget.value) {
              linkTarget.value = elementView.model;
              
              if (linkSource.value === linkTarget.value) {
                  alert('The starting point and the ending point are the same, please redraw!');
                  // Reset style
                  linkSource.value.attr('body/stroke', 'black');
                  linkSource.value.attr('body/strokeWidth', 1);
                  linkSource.value = undefined;
                  linkTarget.value = undefined;
              } else {
                  drawLink(linkSource.value, linkTarget.value, graph, paper);
                  // Reset styles
                  linkSource.value.attr('body/stroke', 'black');
                  linkSource.value.attr('body/strokeWidth', 1);
                  
                  linkSource.value = undefined;
                  linkTarget.value = undefined;
                  editorStore.clearTool();
              }
          }
      }
      
      // Logic for merging (if needed)
      // ...
  };


  // --- Drawing Functions ---

  const drawMachine = (x: number, y: number, graph: joint.dia.Graph) => {
    if (project.value.contextDiagram && project.value.contextDiagram.machine) {
        alert('machine already exist!');
        return;
    }
    
    // Create machine entity
    let machine = Machine.newMachine('machine', 'M', x, y, 100, 50);
    
    // Use WebSocket if connected
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        change("add", "mac", null, machine);
    } else {
        // Fallback for offline
        if (!project.value.contextDiagram) project.value.initContexDiagram();
        project.value.contextDiagram.machine = machine;
        if (project.value.problemDiagram && project.value.problemDiagram.contextDiagram) {
            project.value.problemDiagram.contextDiagram.machine = machine;
        }

        // Draw on graph
        drawMachineOnGraph(machine, graph);
    }
  };

  const drawMachineOnGraph = (machine: Machine, graph: joint.dia.Graph) => {
    const machineElement = new MachineElement();
    machineElement.attr({
      label: {
        text: machine.machine_name + '\n(' + machine.machine_shortName + ')',
      },
      r: {
        ref: 'label',
        refX: -35,
        refY: 0,
        x: 0,
        y: 0,
        refWidth: 45,
        refHeight: '120%',
      },
      r1: {
        ref: 'label',
        refX: -20,
        refY: 0,
        x: 0,
        y: 0,
        refWidth: 30,
        refHeight: '120%',
      },
      r2: {
        ref: 'label',
        refX: -5,
        refY: 0,
        x: 0,
        y: 0,
        refWidth: 15,
        refHeight: '120%',
      },
      root: {
        name: 'machine',
        title: machine.machine_name,
        shortName: machine.machine_shortName,
      }
    });
    machineElement.position(machine.machine_x, machine.machine_y);
    machineElement.addTo(graph);
    return machineElement;
  };

  const drawProblemDomain = (x: number, y: number, graph: joint.dia.Graph) => {
    let no, name, shortName;
    while (true) {
      no = problemdomain_no;
      problemdomain_no += 1;
      name = 'problemDomain' + no;
      shortName = 'PD' + no;
      let conflicting_name = false;
      
      if (project.value.contextDiagram && project.value.contextDiagram.problemDomainList) {
        for (let pdi of project.value.contextDiagram.problemDomainList) {
            if (pdi.problemdomain_name == name || pdi.problemdomain_shortname == shortName) {
            conflicting_name = true;
            }
        }
      }
      if (!conflicting_name) {
        break;
      }
    }

    let pd = ProblemDomain.newProblemDomain(no, name, shortName, 'Causal', 'GivenDomain', x, y, 100, 50);
    
    // Use WebSocket if connected
    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        change("add", "pro", null, pd);
    } else {
        // Update project
        if (!project.value.contextDiagram) project.value.initContexDiagram();
        project.value.contextDiagram.problemDomainList.push(pd);

        drawGivenDomain(pd, graph);
    }
  };

  const drawGivenDomain = (givenDomain: ProblemDomain, graph: joint.dia.Graph) => {
    var givenElement = new GivenElement();
    givenElement.attr({
      label: {
        text: givenDomain.problemdomain_name + '\n(' + givenDomain.problemdomain_shortname + ')',
      },
      r: {
        ref: 'label',
        refX: -5,
        refY: -5,
        x: 0,
        y: 0,
        refWidth: 10,
        refHeight: 10,
      },
      r1: {
        ref: 'label',
        refX: -5,
        refY: -5,
        x: 0,
        y: 0,
        refWidth: 10,
        refHeight: 10,
      },
      root: {
        name: 'problemDomain',
        title: givenDomain.problemdomain_name,
        shortName: givenDomain.problemdomain_shortname,
      }
    });
    givenElement.position(givenDomain.problemdomain_x, givenDomain.problemdomain_y);
    givenElement.addTo(graph);
    return givenElement;
  };
  
  const drawDesignDomain = (designDomain: ProblemDomain, graph: joint.dia.Graph) => {
    let element = drawGivenDomain(designDomain, graph);
    element.attr('r/refX', '-15');
    element.attr('r/refWidth', '20');
    return element;
  };

  const drawRequirement = (x: number, y: number, graph: joint.dia.Graph) => {
    let no, name;
    while (true) {
      no = requirement_no;
      requirement_no += 1;
      name = 'requirement' + no;
      let conflicting_name = false;
      
      if (project.value.problemDiagram && project.value.problemDiagram.requirementList) {
          for (let reqi of project.value.problemDiagram.requirementList) {
            if (reqi.requirement_context == name) {
              conflicting_name = true;
            }
          }
      }
      if (!conflicting_name) {
        break;
      }
    }
    
    let req = Requirement.newRequirement(no, name, x, y, 100, 50);
    
    if (!project.value.problemDiagram) project.value.initProblemDiagram();
    project.value.problemDiagram.requirementList.push(req);

    drawRequirementElement(req, graph);
  };

  const drawRequirementElement = (requirement: Requirement, graph: joint.dia.Graph) => {
    let requirementElement = new joint.shapes.standard.Ellipse();
    requirementElement.attr({
      root: {
        name: 'requirement',
        title: requirement.requirement_context,
        shortName: requirement.requirement_context,
      },
      label: {
        text: requirement.requirement_context,
        fontSize: 25,
        textAnchor: 'middle',
        textVerticalAnchor: 'middle',
      },
      body: {
        ref: 'label',
        refX: 0,
        refY: 0,
        refRx: '60%',
        refRy: '70%',
        refCx: '50%',
        refCy: '50%',
        fill: 'rgb(240,255,255)',
        strokeWidth: 1,
        strokeDasharray: '8,4'
      }
    });
    requirementElement.position(requirement.requirement_x, requirement.requirement_y);
    requirementElement.addTo(graph);
    return requirementElement;
  };

  const getNode = (sgName: string, element: any) => {
    // 1. Try to get from userData (Preferred, handles duplicate IDs)
    const userData = element.get('userData');
    if (userData) {
      return userData;
    }

    // 2. Fallback to title matching (Legacy support)
    let node;
    const nodeName = element.attr('root').title;
    const sgList = project.value.scenarioGraphList;
    let sg: ScenarioGraph | undefined;
    
    if (sgList) {
      for (let i = 0; i < sgList.length; i++) {
        if (sgList[i].title === sgName) {
          sg = sgList[i];
          break;
        }
      }
    }

    if (!sg) return undefined;

    const intNodeList = sg.intNodeList;
    const ctrlNodeList = sg.ctrlNodeList;

    if (intNodeList) {
      for (let i = 0; i < intNodeList.length; i++) {
        const intNode = intNodeList[i];
        if (intNode.node_type + intNode.node_no === nodeName) {
          node = intNode;
          return node;
        }
      }
    }
    
    if (ctrlNodeList) {
      for (let i = 0; i < ctrlNodeList.length; i++) {
        const ctrlNode = ctrlNodeList[i];
        if (ctrlNode.node_type + ctrlNode.node_no === nodeName) {
          node = ctrlNode;
          break;
        }
      }
    }
    return node;
  };

  const getLineMax = (lineList: Line[]) => {
    let max = 0;
    if (!lineList) {
      return 0;
    }
    for (let i = 0; i < lineList.length; i++) {
      if (lineList[i].line_no > max) {
        max = lineList[i].line_no;
      }
    }
    return max;
  };

  const saveLine = (sgName: string, line: Line) => {
    const sgList = project.value.scenarioGraphList;
    if (sgList) {
      for (let i = 0; i < sgList.length; i++) {
        if (sgList[i].title === sgName) {
          let lineList = sgList[i].lineList;
          if (!lineList) {
            lineList = [];
            lineList.push(line);
          } else {
            lineList.push(line);
          }
          sgList[i].lineList = lineList;
          break;
        }
      }
    }
  };

  const ctrlType = ['Start', 'End', 'Decision', 'Merge', 'Branch', 'Delay'];

  const getBehLine = (sgName: string, fromNode: Node, toNode: Node): Line | null => {
    let line: Line | null = null;
    const sgList = project.value.scenarioGraphList;
    let sg: ScenarioGraph | undefined;
    let from: Node | undefined = undefined;
    let to: Node | undefined = undefined;

    if (sgList) {
        for (let i = 0; i < sgList.length; i++) {
        if (sgList[i].title === sgName) {
            sg = sgList[i];
            break;
        }
        }
    }

    if (!sg || !sg.lineList) return null;
    const lineList = sg.lineList;

    for (let l of lineList) {
      if (l.line_type == "BehOrder" || l.line_type == "ExpOrder") {
        continue;
      }
      if ((l.fromNode.node_type == fromNode.node_type && l.fromNode.node_no == fromNode.node_no)
        || (l.toNode.node_type == fromNode.node_type && l.toNode.node_no == fromNode.node_no)) {
        if (l.fromNode.node_type == fromNode.node_type
          && l.fromNode.node_no == fromNode.node_no) {
          from = l.toNode;
        } else if (l.toNode.node_type == fromNode.node_type
          && l.toNode.node_no == fromNode.node_no) {
          from = l.fromNode;
        }
      } else if ((l.fromNode.node_type == toNode.node_type && l.fromNode.node_no == toNode.node_no)
        || (l.toNode.node_type == toNode.node_type && l.toNode.node_no == toNode.node_no)) {
        if (l.fromNode.node_type == toNode.node_type
          && l.fromNode.node_no == toNode.node_no) {
          to = l.toNode;
        } else if (l.toNode.node_type == toNode.node_type
          && l.toNode.node_no == toNode.node_no) {
          to = l.fromNode;
        }
      }
    }
    
    if (from && to) {
      for (let l of lineList) {
        if ((l.fromNode.node_type == from.node_type && l.fromNode.node_no == from.node_no)
          && (l.toNode.node_type == to.node_type && l.toNode.node_no == to.node_no)) {
          return null;
        }
      }
      line = new Line();
      line.fromNode = from;
      line.toNode = to;
      line.line_type = "BehOrder";
      line.turnings = "";
      line.line_no = getLineMax(lineList) + 1;
    }
    return line;
  };

  const getBehLine_ctrl = (sgName: string, fromNode: Node, toNode: Node): Line | null => {
    let line: Line | null = null;
    const sgList = project.value.scenarioGraphList;
    let sg: ScenarioGraph | undefined;
    let from: Node | undefined = undefined;
    let to: Node | undefined = undefined;
    
    if (sgList) {
        for (let i = 0; i < sgList.length; i++) {
        if (sgList[i].title === sgName) {
            sg = sgList[i];
            break;
        }
        }
    }
    
    if (!sg || !sg.lineList) return null;
    const lineList = sg.lineList;

    if (ctrlType.indexOf(fromNode.node_type) !== -1 && ctrlType.indexOf(toNode.node_type) !== -1) {
      from = new Node();
      to = new Node();
      Object.assign(from, fromNode);
      Object.assign(to, toNode);
      from.node_no = fromNode.node_no + 1;
      to.node_no = toNode.node_no + 1;
    } else if (ctrlType.indexOf(fromNode.node_type) !== -1) {
      from = new Node();
      Object.assign(from, fromNode);
      from.node_no = fromNode.node_no + 1;
    } else if (ctrlType.indexOf(toNode.node_type) !== -1) {
      to = new Node();
      Object.assign(to, toNode);
      to.node_no = toNode.node_no + 1;
    }
    
    for (let l of lineList) {
      if (l.line_type == "BehOrder" || l.line_type == "ExpOrder") {
        continue;
      }
      if ((l.fromNode.node_type == fromNode.node_type && l.fromNode.node_no == fromNode.node_no)
        || (l.toNode.node_type == fromNode.node_type && l.toNode.node_no == fromNode.node_no)) {
        if (l.fromNode.node_type == fromNode.node_type
          && l.fromNode.node_no == fromNode.node_no) {
          from = l.toNode;
        } else if (l.toNode.node_type == fromNode.node_type
          && l.toNode.node_no == fromNode.node_no) {
          from = l.fromNode;
        }
      } else if ((l.fromNode.node_type == toNode.node_type && l.fromNode.node_no == toNode.node_no)
        || (l.toNode.node_type == toNode.node_type && l.toNode.node_no == toNode.node_no)) {
        if (l.fromNode.node_type == toNode.node_type
          && l.fromNode.node_no == toNode.node_no) {
          to = l.toNode;
        } else if (l.toNode.node_type == toNode.node_type
          && l.toNode.node_no == toNode.node_no) {
          to = l.fromNode;
        }
      }
    }
    
    if (from && to) {
      for (let l of lineList) {
        if ((l.fromNode.node_type == from.node_type && l.fromNode.node_no == from.node_no)
          && (l.toNode.node_type == to.node_type && l.toNode.node_no == to.node_no)) {
          return null;
        }
      }
      line = new Line();
      line.fromNode = from;
      line.toNode = to;
      line.line_type = "BehOrder";
      line.turnings = "";
      line.line_no = getLineMax(lineList) + 1;
    }
    return line;
  };

  const drawLink = (source: any, target: any, graph: joint.dia.Graph, paper: joint.dia.Paper) => {
      const tool = editorStore.tool;
      if (!tool) return;

      const sourceType = source.attr('root').name;
      const targetType = target.attr('root').name;

      if (tool === 'Interface') {
          if (sourceType === "requirement" || targetType === "requirement")
              alert("interface should connect machine(problemDomain) and problemDomain");
          else {
              drawInterface(source, target, graph);
          }
      } else if (tool === 'Reference') {
          if ((sourceType === "problemDomain" && targetType === "requirement") ||
              (sourceType === "requirement" && targetType === "problemDomain")) {
              drawReference(source, target, graph);
          }
          else alert("Reference should connect problemDomain and requirement");

      } else if (tool === 'Constraint') {
          if ((sourceType === "problemDomain" && targetType === "requirement") ||
              (sourceType === "requirement" && targetType === "problemDomain")) {
              drawConstraint(source, target, graph);
          } else alert("Constraint should connect problemDomain and requirement");
      } else if (['BehOrder', 'BehEnable', 'Synchrony', 'ExpOrder', 'ExpEnable'].includes(tool)) {
          const paperId = (paper as any).id;
          if (!paperId) return;
          const sgName = paperId.substring(0, paperId.length - 1);
          
          const fromNode = getNode(sgName, source);
          const toNode = getNode(sgName, target);

          if (!fromNode || !toNode) {
              console.warn("Could not find nodes for connection");
              return;
          }

          let lineList: Line[] = [];
          const sgList = project.value.scenarioGraphList;
          if (sgList) {
             for (let i = 0; i < sgList.length; i++) {
                if (sgList[i].title === sgName) {
                   lineList = sgList[i].lineList || [];
                   break;
                }
             }
          }
          
          const lineNo = getLineMax(lineList) + 1;

          let line = {
             line_type: tool,
             line_no: lineNo, 
             fromNode: fromNode, 
             toNode: toNode,
             condition: '',
             turnings: ''
          } as unknown as Line;
          
          const res = drawLine(line, source, target, graph, paper);
          if (res) {
              saveLine(sgName, line);
          }

          // --- Synchronization Logic ---
          // Determine the type of the synchronized line
          let syncType = "";
          if (line.line_type === "ExpOrder") syncType = "BehOrder";
          else if (line.line_type === "BehOrder") syncType = "ExpOrder";

          if (syncType) {
              const findPartner = (node: Node): Node | null => {
                  // 1. Control Nodes: Check userData or use heuristic
                  if (ctrlType.indexOf(node.node_type) !== -1) {
                      // Try userData first
                      const element = graph.getElements().find(el => {
                          const ud = el.get('userData');
                          return ud && ud.node_type === node.node_type && ud.node_no === node.node_no;
                      });
                      if (element) {
                          const ud = element.get('userData');
                          if (ud && ud.partner_no) {
                              return { node_type: ud.partner_type || node.node_type, node_no: ud.partner_no } as Node;
                          }
                      }
                      // Heuristic: Left (x < 300) -> +1, Right (x >= 300) -> -1
                      // Assuming standard layout
                      if (node.node_x < 300) return { node_type: node.node_type, node_no: node.node_no + 1 } as Node;
                      else return { node_type: node.node_type, node_no: node.node_no - 1 } as Node;
                  } 
                  // 2. Interaction Nodes: Check connections (Synchrony/Enable)
                  else {
                      // Reuse getBehLine logic but extract just the partner node
                      // getBehLine returns a Line connecting 'partner' to 'other partner'
                      // logic: fromNode -> (link) -> partner
                      const sgList = project.value.scenarioGraphList;
                      let sg = sgList?.find(s => s.title === sgName);
                      if (sg && sg.lineList) {
                          for (let l of sg.lineList) {
                              if (l.line_type === "BehOrder" || l.line_type === "ExpOrder") continue;
                              if (l.fromNode.node_type === node.node_type && l.fromNode.node_no === node.node_no) return l.toNode;
                              if (l.toNode.node_type === node.node_type && l.toNode.node_no === node.node_no) return l.fromNode;
                          }
                      }
                  }
                  return null;
              };

              const partnerFrom = findPartner(fromNode);
              const partnerTo = findPartner(toNode);

              if (partnerFrom && partnerTo) {
                  // Create synchronized line
                  let newLine = new Line();
                  newLine.fromNode = partnerFrom;
                  newLine.toNode = partnerTo;
                  newLine.line_type = syncType;
                  newLine.turnings = "";
                  
                  // Get Max Line No
                  let lineList: Line[] = [];
                  const sgList = project.value.scenarioGraphList;
                  if (sgList) {
                     for (let i = 0; i < sgList.length; i++) {
                        if (sgList[i].title === sgName) {
                           lineList = sgList[i].lineList || [];
                           break;
                        }
                     }
                  }
                  newLine.line_no = getLineMax(lineList) + 1;

                  // Find elements to draw
                  let fromElement = null;
                  let toElement = null;
                  const elements = graph.getElements();
                  
                  const fromTitle = newLine.fromNode.node_type + newLine.fromNode.node_no;
                  const toTitle = newLine.toNode.node_type + newLine.toNode.node_no;

                  for (let nodeElement of elements) {
                    const root = nodeElement.attr('root');
                    if (root && root.title === fromTitle) fromElement = nodeElement;
                    else if (root && root.title === toTitle) toElement = nodeElement;
                  }
                  
                  if (fromElement && toElement) {
                       const res = drawLine(newLine, fromElement, toElement, graph, paper);
                       if (res) {
                         saveLine(sgName, newLine);
                       }
                  }
              }
          }
      }
  };

  const getname = () => {
      // Simple generator for link names (a, b, ..., z, aa, ab...)
      // Placeholder for now
      return 'a';
  };

  const drawInterface = (source: any, target: any, graph: joint.dia.Graph) => {
    let no;
    while (true) {
        no = interface_no;
        interface_no += 1;
        let conflicting_no = false;
        if (project.value.contextDiagram && project.value.contextDiagram.interfaceList) {
            for (let inti of project.value.contextDiagram.interfaceList) {
                if (inti.interface_no == no) {
                    conflicting_no = true;
                }
            }
        }
        if (!conflicting_no) break;
    }
    
    let name = getname(); // Simplification
    let from = source.attr('root').shortName;
    let to = target.attr('root').shortName;
    let myinterface = Interface.newInterface(no, name, name + '?', from, to, [], 0, 0, 0, 0);
    
    if (!project.value.contextDiagram) project.value.initContexDiagram();
    project.value.contextDiagram.interfaceList.push(myinterface);
    
    drawInterfaceElement(myinterface, source, target, graph);
  };

  const drawInterfaceElement = (int: Interface, source: any, target: any, graph: joint.dia.Graph) => {
    var link = new joint.shapes.standard.Link({
      source: { id: source.id },
      target: { id: target.id }
    });
    link.attr({
      root: {
        name: 'interface',
        title: int.interface_name,
        no: int.interface_no
      },
      line: {
        strokeWidth: 1,
        targetMarker: {
          'fill': 'none',
          'stroke': 'none',
        }
      },
    });
    link.appendLabel({
      attrs: {
        text: {
          text: int.interface_name,
          fontSize: 25,
          textAnchor: 'middle',
          textVerticalAnchor: 'middle',
          background: 'none'
        }
      }
    });
    link.addTo(graph);
    return link;
  };
  
  const drawReference = (source: any, target: any, graph: joint.dia.Graph) => {
      // Similar to drawInterface but for References
      // Simplified for now
      let name = getname();
      let from = source.attr('root').shortName;
      let to = target.attr('root').shortName;
      // ... create entity and draw
      drawGenericLink(source, target, 'reference', name, graph);
  };
  
  const drawConstraint = (source: any, target: any, graph: joint.dia.Graph) => {
      // Similar to drawInterface but for Constraints
      // Simplified for now
      let name = getname();
      drawGenericLink(source, target, 'constraint', name, graph);
  };

  const drawGenericLink = (source: any, target: any, type: string, name: string, graph: joint.dia.Graph) => {
    var link = new joint.shapes.standard.Link({
      source: { id: source.id },
      target: { id: target.id }
    });
    link.attr({
      root: {
        name: type,
        title: name,
      },
      line: {
        strokeWidth: 1,
        strokeDasharray: '4 2', // Dashed for ref/constraint usually
        targetMarker: {
          'fill': 'none',
          'stroke': 'none',
        }
      },
    });
    link.appendLabel({
      attrs: {
        text: {
          text: name,
          fontSize: 25,
          textAnchor: 'middle',
          textVerticalAnchor: 'middle',
          background: 'none'
        }
      }
    });
    link.addTo(graph);
    return link;
  };


  const drawCtrlNode = (id: string, ctrlNode: CtrlNode, graph: joint.dia.Graph) => {
    let ctrlElement;
    let r = 10;
    
    switch (ctrlNode.node_type) {
      case 'Start':
        ctrlElement = new joint.shapes.standard.Circle();
        ctrlElement.resize(2 * r, 2 * r);
        ctrlElement.attr({
          body: {
            fill: 'black',
          }
        });
        startNodeList.push(ctrlElement);
        break;
      case 'End':
        ctrlElement = new EndElement();
        ctrlElement.attr({
          body: {
            ref: 'c1',
            refRCircumscribed: '70%',
          },
          c1: {
            r: r * 0.7,
          }
        });
        endNodeList.push(ctrlElement);
        break;
      case 'Decision':
        ctrlElement = new joint.shapes.standard.Polygon();
        ctrlElement.attr({
          body: {
            refPoints: '0,10 10,0 20,10 10,20',
            strokeWidth: 1,
            fill: 'rgb(240,255,255)',
          },
          label: {
            text: ctrlNode.node_text,
            fontSize: 20,
          }
        });
        ctrlElement.resize(40, 40);
        decisionNodeList.push(ctrlElement);
        break;
      case 'Merge':
        ctrlElement = new joint.shapes.standard.Polygon();
        ctrlElement.attr({
          body: {
            refPoints: '0,10 10,0 20,10 10,20',
            fill: 'rgb(240,255,255)',
          },
        });
        ctrlElement.resize(40, 40);
        mergeNodeList.push(ctrlElement);
        break;
      case 'Branch':
        ctrlElement = new joint.shapes.standard.Rectangle();
        ctrlElement.attr({
          body: {
            fill: 'black',
          }
        });
        ctrlElement.resize(150, 5);
        branchNodeList.push(ctrlElement);
        break;
      case 'Delay':
        ctrlElement = new joint.shapes.standard.Polygon();
        if (ctrlNode.delay_type == "at") {
          ctrlElement.attr({
            label: {
              text: 'at ' + ctrlNode.node_text,
            },
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        } else if (ctrlNode.delay_type == "after") {
          ctrlElement.attr({
            label: {
              text: 'after ' + ctrlNode.node_text,
            },
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        } else {
          ctrlElement.attr({
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        }
        ctrlElement.resize(40, 40);
        delayNodeList.push(ctrlElement);
        break;
    }

    if (ctrlElement) {
      ctrlElement.attr({
        root: {
          title: ctrlNode.node_type + ctrlNode.node_no,
        },
      });
      // Bind data to element
      ctrlElement.set('userData', ctrlNode);
      
      ctrlElement.position(ctrlNode.node_x, ctrlNode.node_y);
      ctrlElement.addTo(graph);

      ctrlElement.on('change:position', function (element1: any, position: any) {
        ctrlNode.node_x = position.x;
        ctrlNode.node_y = position.y;
        saveCtrlNode(id.substring(0, id.length - 1), ctrlNode);
      });
    }
    return ctrlElement;
  };

  const drawCtrlNode_clone = (id: string, ctrlNode: CtrlNode, ctrlNode_clone: CtrlNode, graph: joint.dia.Graph) => {
    let ctrlElement;
    let ctrlElement_clone;
    let r = 10;
    
    switch (ctrlNode.node_type) {
      case 'Start':
        ctrlElement = new joint.shapes.standard.Circle();
        ctrlElement.resize(2 * r, 2 * r);
        ctrlElement.attr({
          body: {
            fill: 'black',
          }
        });
        ctrlElement_clone = ctrlElement.clone();
        startNodeList.push(ctrlElement);
        startNodeList.push(ctrlElement_clone);
        break;
      case 'End':
        ctrlElement = new EndElement();
        ctrlElement.attr({
          body: {
            ref: 'c1',
            refRCircumscribed: '70%',
          },
          c1: {
            r: r * 0.7,
          }
        });
        ctrlElement_clone = ctrlElement.clone();
        endNodeList.push(ctrlElement);
        endNodeList.push(ctrlElement_clone);
        break;
      case 'Decision':
        ctrlElement = new joint.shapes.standard.Polygon();
        ctrlElement.attr({
          body: {
            refPoints: '0,10 10,0 20,10 10,20',
            strokeWidth: 1,
            fill: 'rgb(240,255,255)',
          },
          label: {
            text: ctrlNode.node_text,
            fontSize: 20,
          }
        });
        ctrlElement.resize(40, 40);
        ctrlElement_clone = ctrlElement.clone();
        decisionNodeList.push(ctrlElement);
        decisionNodeList.push(ctrlElement_clone);
        break;
      case 'Merge':
        ctrlElement = new joint.shapes.standard.Polygon();
        ctrlElement.attr({
          body: {
            refPoints: '0,10 10,0 20,10 10,20',
            fill: 'rgb(240,255,255)',
          },
        });
        ctrlElement.resize(40, 40);
        ctrlElement_clone = ctrlElement.clone();
        mergeNodeList.push(ctrlElement);
        mergeNodeList.push(ctrlElement_clone);
        break;
      case 'Branch':
        ctrlElement = new joint.shapes.standard.Rectangle();
        ctrlElement.attr({
          body: {
            fill: 'black',
          }
        });
        ctrlElement.resize(150, 5);
        ctrlElement_clone = ctrlElement.clone();
        branchNodeList.push(ctrlElement);
        branchNodeList.push(ctrlElement_clone);
        break;
      case 'Delay':
        ctrlElement = new joint.shapes.standard.Polygon();
        if (ctrlNode.delay_type == "at") {
          ctrlElement.attr({
            label: {
              text: 'at ' + ctrlNode.node_text,
            },
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        } else if (ctrlNode.delay_type == "after") {
          ctrlElement.attr({
            label: {
              text: 'after ' + ctrlNode.node_text,
            },
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        } else {
          ctrlElement.attr({
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        }
        ctrlElement.resize(40, 40);
        ctrlElement_clone = ctrlElement.clone();
        delayNodeList.push(ctrlElement);
        delayNodeList.push(ctrlElement_clone);
        break;
    }

    if (ctrlElement && ctrlElement_clone) {
        ctrlElement.attr({
            root: {
                title: ctrlNode.node_type + ctrlNode.node_no,
                title_clone: ctrlNode_clone.node_type + ctrlNode_clone.node_no,
            },
        });
        ctrlElement_clone.attr({
            root: {
                title: ctrlNode_clone.node_type + ctrlNode_clone.node_no,
                title_clone: ctrlNode.node_type + ctrlNode.node_no,
            },
        });
        ctrlElement.position(ctrlNode.node_x, ctrlNode.node_y);
        ctrlElement.addTo(graph);
        ctrlElement_clone.position(ctrlNode_clone.node_x, ctrlNode_clone.node_y);
        ctrlElement_clone.addTo(graph);

        ctrlElement.on('change:position', function (element1: any, position: any) {
            ctrlNode.node_x = position.x;
            ctrlNode.node_y = position.y;
            // saveCtrlNode_clone(id.substring(0, id.length - 1), ctrlNode, ctrlNode_clone);
        });
        ctrlElement_clone.on('change:position', function (element1: any, position: any) {
            ctrlNode_clone.node_x = position.x;
            ctrlNode_clone.node_y = position.y;
            // saveCtrlNode_clone(id.substring(0, id.length - 1), ctrlNode, ctrlNode_clone);
        });
    }
    return ctrlElement;
  };


  const saveCtrlNode = (sgName: string, ctrlNode: CtrlNode) => {
    if (!project.value.scenarioGraphList) return;
    const sgList = project.value.scenarioGraphList;
    let exit = false;
    for (let i = 0; i < sgList.length; i++) {
      if (sgList[i].title === sgName) {
        let ctrlNodeList = sgList[i].ctrlNodeList;
        if (!ctrlNodeList) {
          ctrlNodeList = [];
          ctrlNodeList.push(ctrlNode);
        } else {
          for (let j = 0; j < ctrlNodeList.length; j++) {
            if ((ctrlNodeList[j].node_type + ctrlNodeList[j].node_no) === (ctrlNode.node_type + ctrlNode.node_no)) {
              ctrlNodeList[j] = ctrlNode;
              exit = true;
              break;
            }
          }
          if (!exit) {
            ctrlNodeList.push(ctrlNode);
          }
        }
        sgList[i].ctrlNodeList = ctrlNodeList;
        break;
      }
    }
  };

  const saveInt = (sgName: string, intNode: any) => {
    const sgList = project.value.scenarioGraphList;
    let exit = false;
    for (let i = 0; i < sgList.length; i++) {
      if (sgList[i].title === sgName) {
        let intNodeList = sgList[i].intNodeList;
        if (!intNodeList) {
          intNodeList = [];
          intNodeList.push(intNode);
        } else {
          for (let j = 0; j < intNodeList.length; j++) {
            if ((intNodeList[j].node_type + intNodeList[j].node_no) === (intNode.node_type + intNode.node_no)) {
              intNodeList[j] = intNode;
              exit = true;
              break;
            }
          }
          if (!exit) {
            intNodeList.push(intNode);
          }
        }
        sgList[i].intNodeList = intNodeList;
        break;
      }
    }
  };

  const drawIntNode = (id: string, intNode: any, graph: joint.dia.Graph) => {
    let textElement = new joint.shapes.standard.Rectangle();
    textElement.attr({
      body: {
        ref: 'label',
        refX: -5,
        refY: 0,
        x: 0,
        y: 0,
        refWidth: 10,
        refHeight: '120%',
        fill: 'none',
        stroke: 'none',
        strokeWidth: 1,
      },
      root: {
        title: intNode.node_type + intNode.node_no + 'text',
        id: id,
      }
    });
    // Bind data to element
    textElement.set('userData', intNode);
    
    textElement.position(intNode.node_x + 80, intNode.node_y - 30);
    textElement.addTo(graph);

    // Simplified text setting using pre_condition if available
    if (intNode.pre_condition) {
        textElement.attr({
          label: {
            text: intNode.pre_condition.phenomenon_name,
            fontSize: 20,
            textAnchor: 'middle',
            textVerticalAnchor: 'middle',
          }
        });
    } else {
        // Fallback to searching by ID (legacy behavior)
        const phes = getPhenomenon(project.value);
        let text = '';
        for (let i = 0; i < phes.length; i++) {
          if (phes[i].phenomenon_no === intNode.node_no) {
            text = phes[i].phenomenon_name;
            textElement.attr({
              label: {
                text: text,
                fontSize: 20,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle',
              }
            });
          }
        }
    }

    let intElement = new joint.shapes.standard.Rectangle();
    intElement.attr({
      body: {
        ref: 'label',
        refX: 0,
        refY: 0,
        x: -10,
        y: -5,
        rx: 20,
        ry: 20,
        refWidth: 20,
        refHeight: 10,
        fill: 'rgb(135,206,250)',
        strokeWidth: 1,
      },
      label: {
        text: 'int' + intNode.node_no,
        fontSize: 25,
        textAnchor: 'middle',
        textVerticalAnchor: 'middle',
      }
    });

    if (intNode.pre_condition) {
       intElement.attr({
        label: {
          text: '* int' + intNode.node_no,
        }
      });
    }
    if (intNode.post_condition) {
       intElement.attr({
        label: {
          text: 'int' + intNode.node_no + '*',
        }
      });
    }

    if (intNode.node_type === 'BehInt') {
      intElement.attr({
        body: {
          fill: 'rgb(189,215,238)', // Blue
        }
      });
    } else if (intNode.node_type === 'ConnInt') {
      intElement.attr({
        body: {
          fill: 'rgb(197,224,180)',
        }
      });
    } else if (intNode.node_type === 'ExpInt') {
      intElement.attr({
        body: {
          fill: 'rgb(255,230,153)', // Yellow
          strokeDasharray: '8,2',
        }
      });
    }

    intElement.attr({
      root: {
        title: intNode.node_type + intNode.node_no,
      },
    });
    // Bind data to element
    intElement.set('userData', intNode);

    intElement.position(intNode.node_x, intNode.node_y);
    intElement.addTo(graph);

    intElement.on('change:position', function (element1: any, position: any) {
      intNode.node_x = position.x;
      intNode.node_y = position.y;
      textElement.position(intNode.node_x + 80, intNode.node_y - 30);
      saveInt(id.substring(0, id.length - 1), intNode);
    });
    
    return intElement;
  };

  const drawConditionNode = (id: string, intNode: any, graph: joint.dia.Graph) => {
    let textElement = new joint.shapes.standard.Rectangle();
    textElement.attr({
      body: {
        ref: 'label',
        refX: -5,
        refY: 0,
        x: 0,
        y: 0,
        refWidth: 10,
        refHeight: '120%',
        fill: 'none',
        stroke: 'none',
        strokeWidth: 1,
      },
      root: {
        title: intNode.node_type + intNode.node_no + 'text',
        id: id,
      }
    });
    // Bind data to element
    textElement.set('userData', intNode);

    textElement.position(intNode.node_x + 80, intNode.node_y - 30);
    textElement.addTo(graph);

    // Simplified text setting using pre_condition if available
    if (intNode.pre_condition) {
        textElement.attr({
          label: {
            text: intNode.pre_condition.phenomenon_name,
            fontSize: 20,
            textAnchor: 'middle',
            textVerticalAnchor: 'middle',
          }
        });
    } else {
        const phes = getPhenomenon(project.value);
        let text = '';
        for (let i = 0; i < phes.length; i++) {
          if (phes[i].phenomenon_no === intNode.node_no) {
            text = phes[i].phenomenon_name;
            textElement.attr({
              label: {
                text: text,
                fontSize: 20,
                textAnchor: 'middle',
                textVerticalAnchor: 'middle',
              }
            });
          }
        }
    }

    let intElement = new joint.shapes.standard.Rectangle();
    intElement.attr({
      body: {
        ref: 'label',
        refX: 0,
        refY: 0,
        x: -10,
        y: -5,
        refWidth: 20,
        refHeight: 10,
        fill: 'rgb(135,206,250)',
        strokeWidth: 1,
      },
      label: {
        text: 'int' + intNode.node_no,
        fontSize: 25,
        textAnchor: 'middle',
        textVerticalAnchor: 'middle',
      }
    });

    intElement.attr({
      root: {
        title: intNode.node_type + intNode.node_no,
      },
    });
    // Bind data to element
    intElement.set('userData', intNode);

    intElement.position(intNode.node_x, intNode.node_y);
    intElement.addTo(graph);

    intElement.on('change:position', function (element1: any, position: any) {
      intNode.node_x = position.x;
      intNode.node_y = position.y;
      textElement.position(intNode.node_x + 80, intNode.node_y - 30);
      saveInt(id.substring(0, id.length - 1), intNode);
    });
    
    return intElement;
  };


  const drawLine = (line: Line, fromElement: any, toElement: any, graph: joint.dia.Graph, paper: joint.dia.Paper) => {
    let link;
    switch (line.line_type) {
      case 'BehOrder':
        link = new joint.shapes.standard.Link();
        link.attr({
          line: {
            stroke: 'blue',
            strokeWidth: 1,
          },
          root: {
            title: line.line_type + line.line_no,
          },
        });
        link.source(fromElement);
        link.target(toElement);
        break;
      case 'BehEnable':
        link = new joint.shapes.standard.Link();
        link.attr({
          line: {
            stroke: 'red',
            strokeWidth: 1,
          },
          root: {
            title: line.line_type + line.line_no,
          },
        });
        link.source(fromElement);
        link.target(toElement);
        break;
      case 'Synchrony':
        link = new joint.shapes.standard.Link();
        link.attr({
          line: {
            stroke: 'green',
            strokeWidth: 1,
            targetMarker: {
              'fill': 'none',
              'stroke': 'none',
            },
          },
          root: {
            title: line.line_type + line.line_no,
          },
        });
        link.source(fromElement);
        link.target(toElement);
        break;
      case 'ExpOrder':
        link = new joint.shapes.standard.Link();
        link.attr({
          line: {
            stroke: 'orange',
            strokeWidth: 1,
            strokeDasharray: '8,4'
          },
          root: {
            title: line.line_type + line.line_no,
          },
        });
        link.source(fromElement);
        link.target(toElement);
        break;
      case 'ExpEnable':
        link = new joint.shapes.standard.Link();
        link.attr({
          line: {
            stroke: 'purple',
            strokeWidth: 1,
          },
          root: {
            title: line.line_type + line.line_no,
          },
        });
        link.source(fromElement);
        link.target(toElement);
        break;
    }

    if (link) {
      if (line.fromNode && line.fromNode.node_type == "Decision") {
        link.labels([{
          attrs: {
            text: {
              text: line.condition
            }
          }
        }]);
      }
      link.addTo(graph);
      return true;
    }
    return false;
  };

  const drawIntNodeList = (intNodeList: Node[], elementList: any[], graph: joint.dia.Graph, id: string) => {
    if (!intNodeList) return;
    for (let i = 0; i < intNodeList.length; i++) {
      let intNode = intNodeList[i];
      if (intNode) {
        let intElement;
        if (intNode.node_type === 'BehInt' || intNode.node_type === 'ConnInt' || intNode.node_type === 'ExpInt') {
          intElement = drawIntNode(id, intNode, graph);
        } else {
          intElement = drawConditionNode(id, intNode, graph);
        }
        elementList.push(intElement);
      }
    }
  };

  const drawCtrlNodeList = (ctrlNodeList: CtrlNode[], elementList: any[], graph: joint.dia.Graph, id: string) => {
    if (!ctrlNodeList) return;
    for (let i = 0; i < ctrlNodeList.length; i++) {
      let ctrlNode = ctrlNodeList[i];
      if (ctrlNode && ctrlNode.node_type) {
        let ctrlElement = drawCtrlNode(id, ctrlNode, graph);
        elementList.push(ctrlElement);
      }
    }
  };

  const drawLineList = (lineList: Line[], elementList: any[], graph: joint.dia.Graph, paper: joint.dia.Paper) => {
    if (!lineList) return;
    for (let i = 0; i < lineList.length; i++) {
      let line = lineList[i];
      if (line) {
        let fromElement, toElement;
        // Find fromElement
        const fromTitle = line.fromNode.node_type + line.fromNode.node_no;
        fromElement = graph.getElements().find(el => {
            const title = el.attr('root/title');
            return title === fromTitle;
        });

        // Find toElement
        const toTitle = line.toNode.node_type + line.toNode.node_no;
        toElement = graph.getElements().find(el => {
            const title = el.attr('root/title');
            return title === toTitle;
        });

        if (fromElement && toElement) {
          drawLine(line, fromElement, toElement, graph, paper);
        }
      }
    }
  };

  // --- Problem Diagram / Context Diagram Drawers ---

  const findElementByRootTitle = (graph: joint.dia.Graph, shortName: string) => {
    const elements = graph.getElements();
    for (const el of elements) {
      const root = el.attr('root');
      if (root && (root.shortName === shortName || root.title === shortName)) {
        return el;
      }
    }
    return null;
  };

  const drawMachine2 = (machine: Machine, elementList: any[], graph: joint.dia.Graph) => {
    const element = drawMachineOnGraph(machine, graph);
    elementList.push(element);
    return element;
  };

  const drawProblemDomains = (problemDomainList: ProblemDomain[], elementList: any[], graph: joint.dia.Graph) => {
    if (!problemDomainList) return;
    for (let pd of problemDomainList) {
      if (pd.problemdomain_type === 'GivenDomain') {
        const el = drawGivenDomain(pd, graph);
        elementList.push(el);
      } else {
        const el = drawDesignDomain(pd, graph);
        elementList.push(el);
      }
    }
  };

  const drawInterfaces = (interfaceList: Interface[], elementList: any[], graph: joint.dia.Graph) => {
    if (!interfaceList) return;
    for (let int of interfaceList) {
        const sourceElement = findElementByRootTitle(graph, int.interface_from);
        const targetElement = findElementByRootTitle(graph, int.interface_to);
        
        if (sourceElement && targetElement) {
            const el = drawInterfaceElement(int, sourceElement, targetElement, graph);
            elementList.push(el);
        }
    }
  };

  const drawRequirements = (requirementList: Requirement[], elementList: any[], graph: joint.dia.Graph) => {
      if (!requirementList) return;
      for (let req of requirementList) {
          const el = drawRequirementElement(req, graph);
          elementList.push(el);
      }
  };

  const drawConstraints = (constraintList: Constraint[], elementList: any[], reqEleList: any[], graph: joint.dia.Graph) => {
      if (!constraintList) return;
      for (let con of constraintList) {
          const sourceElement = findElementByRootTitle(graph, con.constraint_from);
          const targetElement = findElementByRootTitle(graph, con.constraint_to);
           if (sourceElement && targetElement) {
            const el = drawGenericLink(sourceElement, targetElement, 'constraint', con.constraint_name, graph);
            elementList.push(el);
        }
      }
  };

  const drawReferences = (referenceList: Reference[], elementList: any[], reqEleList: any[], graph: joint.dia.Graph) => {
      if (!referenceList) return;
      for (let ref of referenceList) {
          const sourceElement = findElementByRootTitle(graph, ref.reference_from);
          const targetElement = findElementByRootTitle(graph, ref.reference_to);
           if (sourceElement && targetElement) {
            const el = drawGenericLink(sourceElement, targetElement, 'reference', ref.reference_name, graph);
            elementList.push(el);
        }
      }
  };

  const drawSubProblemDiagram = (subProblemDiagram: SubProblemDiagram, idx: number) => {
    const elementList: any[] = [];
    const reqEleList: any[] = [];
    const requirement = subProblemDiagram.requirement;
    const id = 'SPD' + idx;
    const el = document.getElementById(id);

    console.log(`[drawSubProblemDiagram] SPD${idx}:`, {
      title: subProblemDiagram.title,
      machine: subProblemDiagram.machine?.machine_name,
      problemDomainCount: subProblemDiagram.problemDomainList?.length,
      problemDomains: subProblemDiagram.problemDomainList?.map((pd: any) => pd.problemdomain_name || pd.problemdomain_shortname),
      requirement: subProblemDiagram.requirement?.requirement_context,
      interfaceCount: subProblemDiagram.interfaceList?.length,
      interfaces: subProblemDiagram.interfaceList?.map((i: any) => i.interface_name),
      constraintCount: subProblemDiagram.constraintList?.length,
      referenceCount: subProblemDiagram.referenceList?.length
    });

    if (!el) return;
    el.innerHTML = ''; // Clear existing content

    const width = el.clientWidth || 1000;
    const height = el.clientHeight || 800;

    const graph = new joint.dia.Graph();
    const paper = new joint.dia.Paper({
      el: el,
      width: width,
      height: height,
      model: graph,
      gridSize: 10,
      drawGrid: true,
      background: {
        color: 'rgb(240,255,255)'
      },
      interactive: true
    });

    // Auto-resize paper to fit content if content is larger than container
    graph.on('add', () => {
        const bbox = graph.getBBox();
        if (bbox) {
            const w = Math.max(width, bbox.x + bbox.width + 50);
            const h = Math.max(height, bbox.y + bbox.height + 50);
            paper.setDimensions(w, h);
        }
    });

    (paper as any).id = id;

    attachPaperEvents(paper, graph, papers.value.length);
    papers.value.push(paper);
    graphs.value.push(graph);

    if (subProblemDiagram.machine)
      drawMachine2(subProblemDiagram.machine, elementList, graph);
    drawProblemDomains(subProblemDiagram.problemDomainList, elementList, graph);
    drawInterfaces(subProblemDiagram.interfaceList, elementList, graph);
    
    if (requirement) {
         reqEleList.push(drawRequirementElement(requirement, graph));
    }
    drawConstraints(subProblemDiagram.constraintList, elementList, reqEleList, graph);
    drawReferences(subProblemDiagram.referenceList, elementList, reqEleList, graph);
    
    paper.on('element:pointerdblclick', function (elementView, evt, x, y) {
      clickedPaper.value = paper;
      var element = elementView.model;
      var name = element.attr('root').name;
      var title = element.attr('root').title;
      console.log(element)
      console.log(name);
      console.log(title);
      if (name === 'machine') {
        // Machine doesn't have a static copy method, so we use JSON clone or manual copy
        if (subProblemDiagram.machine) {
          const m = subProblemDiagram.machine;
          const newMachine = new Machine();
          Object.assign(newMachine, m);
          editingMachine.value = newMachine;
        }
      } else if (name === 'problemDomain') {
        var domainList = subProblemDiagram.problemDomainList;
        for (var i = 0; i < domainList.length; i++) {
          console.log(domainList[i].problemdomain_name)
          if (domainList[i].problemdomain_name === title) {
            editingDomain.value = ProblemDomain.copyProblemDomain(domainList[i]);
          }
        }
      }
    });

    paper.on('link:pointerdblclick', function (linkView, evt, x, y) {
      var link = linkView.model;
      var name = link.attr('root').name;
      var title = link.attr('root').title;
      console.log(link)
      console.log(name);
      
      if (name === 'interface') {
        for (let inte of subProblemDiagram.interfaceList) {
          if (title === inte.interface_name) {
            editingInterface.value = Interface.copyInterface(inte);
          }
        }
      } else if (name === 'constraint') {
        for (let constraint of subProblemDiagram.constraintList) {
          if (title === constraint.constraint_name) {
            editingConstraint.value = Constraint.copyConstraint(constraint);
          }
        }
      } else if (name === 'reference') {
        for (let reference of subProblemDiagram.referenceList) {
          if (title === reference.reference_name) {
            editingReference.value = Reference.copyReference(reference);
          }
        }
      }
    });

    return paper;
  };

  const drawNewSpd = (proj: Project) => {
    const subProblemDiagramList = proj.subProblemDiagramList;
    if (!subProblemDiagramList) return;
    for (let i = 0; i < subProblemDiagramList.length; i++) {
      drawSubProblemDiagram(subProblemDiagramList[i], i);
    }
  };

  const drawContextDiagram1 = (contextDiagram: any) => {
    if (!contextDiagram) return;
    
    // Ensure graph and paper exist
    let graph = graphs.value[0];
    let paper = papers.value[0];

    if (!graph || !paper) {
      const el = document.getElementById("content1");
      if (!el) return;
      
      const width = el.clientWidth;
      const height = el.clientHeight;
      
      graph = new joint.dia.Graph();
      paper = new joint.dia.Paper({
        el: el,
        width: width,
        height: height,
        model: graph,
        gridSize: 10,
        drawGrid: true,
        background: {
          color: 'rgb(240,255,255)'
        },
        clickThreshold: 3
      });
      
      graphs.value[0] = graph;
      papers.value[0] = paper;
      
      attachPaperEvents(paper, graph, 0);
    } else {
      graph.clear();
    }

    const elementList: any[] = [];
    if (contextDiagram.machine)
      drawMachine2(contextDiagram.machine, elementList, graph);
    drawProblemDomains(contextDiagram.problemDomainList, elementList, graph);
    drawInterfaces(contextDiagram.interfaceList, elementList, graph);

    paper.on('element:pointerdblclick', function (elementView, evt, x, y) {
      clickedPaper.value = paper;
      var element = elementView.model;
      var name = element.attr('root').name;
      var title = element.attr('root').title;

      if (name === 'machine') {
        if (project.value.contextDiagram && project.value.contextDiagram.machine) {
           const m = project.value.contextDiagram.machine;
           const newMachine = new Machine();
           Object.assign(newMachine, m);
           editingMachine.value = newMachine;
        }
      } else if (name === 'problemDomain') {
        var domainList = project.value.contextDiagram?.problemDomainList;
        if (domainList) {
          for (var i = 0; i < domainList.length; i++) {
            if (domainList[i].problemdomain_name === title) {
              editingDomain.value = ProblemDomain.copyProblemDomain(domainList[i]);
            }
          }
        }
      }
    });

    paper.on('link:pointerdblclick', function (linkView, evt, x, y) {
      var link = linkView.model;
      var name = link.attr('root').name;
      var title = link.attr('root').title;
      
      if (name === 'interface') {
        const interfaceList = project.value.contextDiagram?.interfaceList;
        if (interfaceList) {
          for (let inte of interfaceList) {
            if (title === inte.interface_name) {
              editingInterface.value = Interface.copyInterface(inte);
            }
          }
        }
      }
    });
  };

  const drawProblemDiagram1 = (problemDiagram: ProblemDiagram) => {
    if (!problemDiagram) return;
    
    // Ensure graph and paper exist
    let graph = graphs.value[1];
    let paper = papers.value[1];

    if (!graph || !paper) {
      const el = document.getElementById("content2");
      if (!el) return;
      
      const width = el.clientWidth;
      const height = el.clientHeight;
      
      graph = new joint.dia.Graph();
      paper = new joint.dia.Paper({
        el: el,
        width: width,
        height: height,
        model: graph,
        gridSize: 10,
        drawGrid: true,
        background: {
          color: 'rgb(240,255,255)'
        },
        clickThreshold: 3
      });
      
      graphs.value[1] = graph;
      papers.value[1] = paper;
      
      attachPaperEvents(paper, graph, 1);
    } else {
      graph.clear();
    }

    const elementList: any[] = [];
    const reqEleList: any[] = [];

    if (problemDiagram.contextDiagram.machine)
      drawMachine2(problemDiagram.contextDiagram.machine, elementList, graph);
    drawProblemDomains(problemDiagram.contextDiagram.problemDomainList, elementList, graph);
    drawInterfaces(problemDiagram.contextDiagram.interfaceList, elementList, graph);
    drawRequirements(problemDiagram.requirementList, elementList, graph);
    drawConstraints(problemDiagram.constraintList, elementList, reqEleList, graph);
    drawReferences(problemDiagram.referenceList, elementList, reqEleList, graph);

    paper.on('element:pointerdblclick', function (elementView, evt, x, y) {
      clickedPaper.value = paper;
      var element = elementView.model;
      var name = element.attr('root').name;
      var title = element.attr('root').title;

      if (name === 'machine') {
        if (project.value.contextDiagram && project.value.contextDiagram.machine) {
           const m = project.value.contextDiagram.machine;
           const newMachine = new Machine();
           Object.assign(newMachine, m);
           editingMachine.value = newMachine;
        }
      } else if (name === 'problemDomain') {
        var domainList = project.value.contextDiagram?.problemDomainList;
        if (domainList) {
          for (var i = 0; i < domainList.length; i++) {
            if (domainList[i].problemdomain_name === title) {
              editingDomain.value = ProblemDomain.copyProblemDomain(domainList[i]);
            }
          }
        }
      } else if (name === 'requirement') {
        var requirementList = project.value.problemDiagram?.requirementList;
        if (requirementList) {
          for (var i = 0; i < requirementList.length; i++) {
            if (requirementList[i].requirement_context === title) {
              editingRequirement.value = Requirement.copyRequirement(requirementList[i]);
            }
          }
        }
      }
    });

    paper.on('link:pointerdblclick', function (linkView, evt, x, y) {
      var link = linkView.model;
      var name = link.attr('root').name;
      var title = link.attr('root').title;
      
      if (name === 'interface') {
        const interfaceList = project.value.contextDiagram?.interfaceList;
        if (interfaceList) {
          for (let inte of interfaceList) {
            if (title === inte.interface_name) {
              editingInterface.value = Interface.copyInterface(inte);
            }
          }
        }
      } else if (name === 'constraint') {
        const constraintList = project.value.problemDiagram?.constraintList;
        if (constraintList) {
          for (let constraint of constraintList) {
            if (title === constraint.constraint_name) {
              editingConstraint.value = Constraint.copyConstraint(constraint);
            }
          }
        }
      } else if (name === 'reference') {
        const referenceList = project.value.problemDiagram?.referenceList;
        if (referenceList) {
          for (let reference of referenceList) {
            if (title === reference.reference_name) {
              editingReference.value = Reference.copyReference(reference);
            }
          }
        }
      }
    });
  };

  const drawNewSenarioGraph = (newScenarioGraph: ScenarioGraph) => {
    const id = newScenarioGraph.title + 'M';
    const el = document.getElementById(id);
    if (!el) return;
    el.innerHTML = ''; // Clear existing content
    
    const width = el.clientWidth || 1000;
    const height = el.clientHeight || 800;

    // Check if graph already exists?
    // For now, create new
    const graph = new joint.dia.Graph();
    const paper = new joint.dia.Paper({
      el: el,
      width: width, 
      height: height,
      model: graph,
      gridSize: 10,
      drawGrid: true,
      background: {
        color: 'rgb(240,255,255)'
      },
      clickThreshold: 1,
      interactive: true
    });
    
    // Auto-resize paper to fit content if content is larger than container
    graph.on('add', () => {
        const bbox = graph.getBBox();
        if (bbox) {
            const w = Math.max(width, bbox.x + bbox.width + 50);
            const h = Math.max(height, bbox.y + bbox.height + 50);
            paper.setDimensions(w, h);
        }
    });
    
    (paper as any).id = id;
    
    attachPaperEvents(paper, graph, papers.value.length);
    papers.value.push(paper);
    graphs.value.push(graph);

    // Add Drag and Drop support
    const domEl = el;
    if (domEl) {
      domEl.ondragover = (e) => {
        e.preventDefault();
      };
      domEl.ondrop = (e) => {
        e.preventDefault();
        const tool = e.dataTransfer?.getData('tool');
        if (tool) {
          const point = paper.clientToLocalPoint({ x: e.clientX, y: e.clientY });
          drawElement(graph, point.x, point.y, paper.id as string, tool);
        }
      };
    }

    const elementList: any[] = [];
    drawIntNodeList(newScenarioGraph.intNodeList, elementList, graph, id);
    drawCtrlNodeList(newScenarioGraph.ctrlNodeList, elementList, graph, id);
    drawLineList(newScenarioGraph.lineList, elementList, graph, paper);
  };
  
  const drawFullSenarioGraph = (fullScenarioGraph: ScenarioGraph) => {
    const id = 'content3'; 
    const el = document.getElementById(id);
    if (!el) return;
    el.innerHTML = ''; // Clear existing content
    
    const width = el.clientWidth || 1000;
    const height = el.clientHeight || 800;
    
    const graph = new joint.dia.Graph();
    const paper = new joint.dia.Paper({
      el: el,
      width: width,
      height: height,
      model: graph,
      gridSize: 10,
      drawGrid: true,
      background: {
        color: 'rgb(240,255,255)'
      },
      clickThreshold: 3
    });

    (paper as any).id = id;

    attachPaperEvents(paper, graph, papers.value.length);
    papers.value.push(paper);
    graphs.value.push(graph);
    
    const elementList: any[] = [];
    drawIntNodeList(fullScenarioGraph.intNodeList, elementList, graph, id);
    drawCtrlNodeList(fullScenarioGraph.ctrlNodeList, elementList, graph, id);
    drawLineList(fullScenarioGraph.lineList, elementList, graph, paper);
  };

  const getRelatedPhe = (phe: Phenomenon) => {
    const sgList = project.value.scenarioGraphList;
    let node: Node | undefined;
    let relatedNode: Node | undefined;
    
    if (sgList) {
        for (let i = 0; i < sgList.length; i++) {
        const intNodeList = sgList[i].intNodeList;
        if (intNodeList) {
            for (let intNode of intNodeList) {
                if (phe.phenomenon_no == intNode.node_no) {
                    node = intNode;
                }
            }
        }
        }

        if (node) {
            for (let i = 0; i < sgList.length; i++) {
                const lineList = sgList[i].lineList;
                if (lineList) {
                    for (let line of lineList) {
                        if (line.line_type == "Synchrony" || line.line_type == "BehEnable" || line.line_type == "ExpEnable") {
                            if (line.fromNode.node_no == node.node_no && line.fromNode.node_type == node.node_type) {
                                relatedNode = line.toNode;
                            } else if (line.toNode.node_no == node.node_no && line.toNode.node_type == node.node_type) {
                                relatedNode = line.fromNode;
                            }
                        }
                    }
                }
            }
        }
    }
    
    if (!relatedNode) return undefined;

    const pheList = getPhenomenon(project.value);
    let relatedPhe: Phenomenon | undefined;
    for (let i = 0; i < pheList.length; i++) {
      let p = pheList[i];
      if (p.phenomenon_no == relatedNode.node_no) {
        relatedPhe = p;
        break;
      }
    }
    return relatedPhe;
  };

  const updateIntCondition = (intNode: Node, condition: string) => {
    if (!clickedPaper.value) return;
    const id = clickedPaper.value.id;
    saveInt(id.substring(0, id.length - 1), intNode);
    const graph = clickedPaper.value.model;
    const elements = graph.getElements();
    for (let nodeElement of elements) {
      if (nodeElement.attr('root/title') === intNode.node_type + intNode.node_no) {
        if (condition === "Pre") {
          nodeElement.attr({
            label: {
              text: '* int' + intNode.node_no,
            }
          });
        } else if (condition === "Post") {
          nodeElement.attr({
            label: {
              text: 'int' + intNode.node_no + ' *',
            }
          });
        }
        break;
      }
    }
  };

  const IntCondition_clone = (intNode: Node, node: Phenomenon, condition: string) => {
    const sgList = project.value.scenarioGraphList;
    let phe = getRelatedPhe(node);
    if (!phe || !sgList) return;
    
    for (let i = 0; i < sgList.length; i++) {
      const lineList = sgList[i].lineList;
      if (lineList) {
        for (let line of lineList) {
            if (line.line_type == "Synchrony" || line.line_type == "BehEnable" || line.line_type == "ExpEnable") {
            if (line.fromNode.node_no == intNode.node_no && line.fromNode.node_type == intNode.node_type) {
                if (condition === "Pre") {
                line.toNode.pre_condition = phe;
                } else if (condition === "Post") {
                line.toNode.post_condition = phe;
                }
                updateIntCondition(line.toNode, condition);
            } else if (line.toNode.node_no == intNode.node_no && line.toNode.node_type == intNode.node_type) {
                if (condition === "Pre") {
                line.fromNode.pre_condition = phe;
                } else if (condition === "Post") {
                line.fromNode.post_condition = phe;
                }
                updateIntCondition(line.fromNode, condition);
            }
            }
        }
      }
    }
  };

  const saveCtrlNode_clone = (sgName: string, ctrlNode: CtrlNode, ctrlNode_clone: CtrlNode) => {
      saveCtrlNode(sgName, ctrlNode);
      saveCtrlNode(sgName, ctrlNode_clone);
  };

  const drawCtrlNode_clone_deprecated = (id: string, ctrlNode: CtrlNode, ctrlNode_clone: CtrlNode, graph: joint.dia.Graph) => {
    let ctrlElement: any;
    let ctrlElement_clone: any;
    let r = 10;
    
    switch (ctrlNode.node_type) {
      case 'Start':
        ctrlElement = new joint.shapes.standard.Circle();
        ctrlElement.resize(2 * r, 2 * r);
        ctrlElement.attr({
          body: {
            fill: 'black',
          }
        });
        ctrlElement_clone = ctrlElement.clone();
        startNodeList.push(ctrlElement);
        startNodeList.push(ctrlElement_clone);
        break;
      case 'End':
        ctrlElement = new EndElement();
        ctrlElement.attr({
          body: {
            ref: 'c1',
            refRCircumscribed: '70%',
          },
          c1: {
            r: r * 0.7,
          }
        });
        ctrlElement_clone = ctrlElement.clone();
        endNodeList.push(ctrlElement);
        endNodeList.push(ctrlElement_clone);
        break;
      case 'Decision':
        ctrlElement = new joint.shapes.standard.Polygon();
        ctrlElement.attr({
          body: {
            refPoints: '0,10 10,0 20,10 10,20',
            strokeWidth: 1,
            fill: 'rgb(240,255,255)',
          },
          label: {
            text: ctrlNode.node_text,
            fontSize: 20,
          }
        });
        ctrlElement.resize(40, 40);
        ctrlElement_clone = ctrlElement.clone();
        decisionNodeList.push(ctrlElement);
        decisionNodeList.push(ctrlElement_clone);
        break;
      case 'Merge':
        ctrlElement = new joint.shapes.standard.Polygon();
        ctrlElement.attr({
          body: {
            refPoints: '0,10 10,0 20,10 10,20',
            fill: 'rgb(240,255,255)',
          },
        });
        ctrlElement.resize(40, 40);
        ctrlElement_clone = ctrlElement.clone();
        mergeNodeList.push(ctrlElement);
        mergeNodeList.push(ctrlElement_clone);
        break;
      case 'Branch':
        ctrlElement = new joint.shapes.standard.Rectangle();
        ctrlElement.attr({
          body: {
            fill: 'black',
          }
        });
        ctrlElement.resize(150, 5);
        ctrlElement_clone = ctrlElement.clone();
        branchNodeList.push(ctrlElement);
        branchNodeList.push(ctrlElement_clone);
        break;
      case 'Delay':
        ctrlElement = new joint.shapes.standard.Polygon();
        if (ctrlNode.delay_type == "at") {
          ctrlElement.attr({
            label: {
              text: 'at ' + ctrlNode.node_text,
            },
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        } else if (ctrlNode.delay_type == "after") {
          ctrlElement.attr({
            label: {
              text: 'after ' + ctrlNode.node_text,
            },
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        } else {
          ctrlElement.attr({
            body: {
              refPoints: '0,0 10,0 0,20 10,20',
              fill: 'rgb(240,255,255)'
            }
          });
        }
        ctrlElement.resize(40, 40);
        ctrlElement_clone = ctrlElement.clone();
        delayNodeList.push(ctrlElement);
        delayNodeList.push(ctrlElement_clone);
        break;
    }
    
    ctrlElement.attr({
      root: {
        title: ctrlNode.node_type + ctrlNode.node_no,
        title_clone: ctrlNode_clone.node_type + ctrlNode_clone.node_no,
      },
    });
    ctrlElement_clone.attr({
      root: {
        title: ctrlNode_clone.node_type + ctrlNode_clone.node_no,
        title_clone: ctrlNode.node_type + ctrlNode.node_no,
      },
    });
    
    ctrlElement.position(ctrlNode.node_x, ctrlNode.node_y);
    ctrlElement.addTo(graph);
    ctrlElement_clone.position(ctrlNode_clone.node_x, ctrlNode_clone.node_y);
    ctrlElement_clone.addTo(graph);

    ctrlElement.on('change:position', function (element1: any, position: any) {
      ctrlNode.node_x = position.x;
      ctrlNode.node_y = position.y;
      saveCtrlNode_clone(id.substring(0, id.length - 1), ctrlNode, ctrlNode_clone);
    });
    ctrlElement_clone.on('change:position', function (element1: any, position: any) {
      ctrlNode_clone.node_x = position.x;
      ctrlNode_clone.node_y = position.y;
      saveCtrlNode_clone(id.substring(0, id.length - 1), ctrlNode, ctrlNode_clone);
    });
    return ctrlElement;
  };

  // --- WebSocket Functions ---
  const wsdealId = (id: number) => {
    if (messageId.value == id) {
      messageId.value += 1;
      console.log("this.messageId=", messageId.value);
    } else {
      console.log("this.messageId=", messageId.value);
      console.log("id = ", id);
    }
  };

  const drawMachinews = (machine: Machine) => {
    if (project.value.problemDiagram && project.value.problemDiagram.contextDiagram) {
        project.value.problemDiagram.contextDiagram.machine = machine;
    }
    if (project.value.contextDiagram) {
        project.value.contextDiagram.machine = machine;
    }
    graphs.value.forEach(graph => {
      drawMachineOnGraph(machine, graph);
    });
  };

  const drawProblemDomainws = (problemDomain: ProblemDomain) => {
    if (project.value.contextDiagram) {
        project.value.contextDiagram.problemDomainList.push(problemDomain);
    }
    graphs.value.forEach(graph => {
      if (problemDomain.problemdomain_type === 'Data Storage') {
        drawDesignDomain(problemDomain, graph);
      } else {
        drawGivenDomain(problemDomain, graph);
      }
    });
  };

  const drawRequirement1 = (requirement: Requirement, graph: joint.dia.Graph) => {
    let requirementElement = new joint.shapes.standard.Ellipse();
    requirementElement.attr({
      root: {
        name: 'requirement',
        title: requirement.requirement_context,
        shortName: requirement.requirement_context,
      },
      label: {
        text: requirement.requirement_context,
        fontSize: 25,
        textAnchor: 'middle',
        textVerticalAnchor: 'middle',
      },
      body: {
        ref: 'label',
        refX: 0,
        refY: 0,
        refRx: '60%',
        refRy: '70%',
        refCx: '50%',
        refCy: '50%',
        fill: 'rgb(240,255,255)',
        strokeWidth: 1,
        strokeDasharray: '8,4'
      }
    });
    requirementElement.position(requirement.requirement_x, requirement.requirement_y);
    requirementElement.addTo(graph);
    return requirementElement;
  };

  const drawRequirementws = (req1: Requirement) => {
    let req = Requirement.copyRequirement(req1);
    if (project.value.problemDiagram) {
      project.value.problemDiagram.requirementList.push(req);
    }
    graphs.value.forEach(graph => {
        drawRequirement1(req, graph);
    });
  };

  const drawInterface1 = (int: Interface, source: joint.dia.Element, target: joint.dia.Element, graph: joint.dia.Graph) => {
    var link = new joint.shapes.standard.Link({
      source: { id: source.id },
      target: { id: target.id }
    });
    link.attr({
      root: {
        name: 'interface',
        title: int.interface_name,
        no: int.interface_no
      },
      line: {
        strokeWidth: 1,
        targetMarker: {
          'fill': 'none',
          'stroke': 'none',
        }
      },
    });
    link.appendLabel({
      attrs: {
        text: {
          text: int.interface_name,
          fontSize: 25,
          textAnchor: 'middle',
          textVerticalAnchor: 'middle',
          background: 'none'
        }
      }
    });
    link.addTo(graph);
    return link;
  };

  const drawInterfaceOnGraph = (int: Interface) => {
    let from = int.interface_from;
    let to = int.interface_to;
    graphs.value.forEach(graph => {
      var elefrom: joint.dia.Element | undefined;
      var eleto: joint.dia.Element | undefined;
      let elementList = graph.getElements();
      for (var j = 0; j < elementList.length; j++) {
        if (elementList[j].attr('root')) {
            if (from == elementList[j].attr('root').shortName) {
              elefrom = elementList[j];
            } else if (to == elementList[j].attr('root').shortName) {
              eleto = elementList[j];
            }
        }
      }
      if (elefrom && eleto) {
          drawInterface1(int, elefrom, eleto, graph);
      }
    });
  };

  const drawInterfacews = (int1: Interface) => {
    let int = Interface.copyInterface(int1);
    project.value.addInterface(int);
    drawInterfaceOnGraph(int);
  };

  const drawReference1 = (reference: Reference, source: joint.dia.Element, target: joint.dia.Element, graph: joint.dia.Graph) => {
    var link = new joint.shapes.standard.Link({
        source: { id: source.id },
        target: { id: target.id }
    });
    link.attr({
      root: {
        name: 'reference',
        title: reference.reference_name,
        no: reference.reference_no
      },
      line: {
        strokeWidth: 1,
        targetMarker: {
          'fill': 'none',
          'stroke': 'none',
        },
        strokeDasharray: '8,4'
      },
    });
    link.appendLabel({
      attrs: {
        text: {
          text: reference.reference_name,
          fontSize: 25,
          textAnchor: 'middle',
          textVerticalAnchor: 'middle',
          background: 'none'
        }
      }
    });
    link.addTo(graph);
    return link;
  };

  const drawReferenceOnGraph = (ref: Reference) => {
    const from = ref.reference_from;
    const to = ref.reference_to;
    graphs.value.forEach(graph => {
      var elefrom: joint.dia.Element | undefined;
      var eleto: joint.dia.Element | undefined;
      let elementList = graph.getElements();
      for (var j = 0; j < elementList.length; j++) {
         if (elementList[j].attr('root')) {
            if (from == elementList[j].attr('root').shortName) {
              elefrom = elementList[j];
            } else if (to === elementList[j].attr('root').shortName) {
              eleto = elementList[j];
            }
         }
      }
      if (elefrom && eleto) {
          drawReference1(ref, elefrom, eleto, graph);
      }
    });
  };

  const drawReferencews = (ref1: Reference) => {
    let ref = Reference.copyReference(ref1);
    project.value.addReference(ref);
    drawReferenceOnGraph(ref);
  };

  const drawConstraint1 = (con: Constraint, source: joint.dia.Element, target: joint.dia.Element, graph: joint.dia.Graph) => {
    var link = new joint.shapes.standard.Link({
        source: { id: source.id },
        target: { id: target.id }
    });
    link.attr({
      root: {
        name: 'constraint',
        title: con.constraint_name,
        no: con.constraint_no

      },
      line: { strokeWidth: 1, strokeDasharray: '8,4' }
    });
    link.appendLabel({
      attrs: {
        text: {
          text: con.constraint_name,
          fontSize: 25,
          textAnchor: 'middle',
          textVerticalAnchor: 'middle',
          background: 'none'
        }
      }
    });
    link.addTo(graph);
    return link;
  };

  const drawConstraintOnGraph = (constraint: Constraint) => {
    const from = constraint.constraint_from;
    const to = constraint.constraint_to;
    graphs.value.forEach(graph => {
      var elefrom: joint.dia.Element | undefined;
      var eleto: joint.dia.Element | undefined;
      let elementList = graph.getElements();
      for (var j = 0; j < elementList.length; j++) {
        if (elementList[j].attr('root')) {
            if (from == elementList[j].attr('root').shortName) {
              elefrom = elementList[j];
            } else if (to === elementList[j].attr('root').shortName) {
              eleto = elementList[j];
            }
        }
      }
      if (elefrom && eleto) {
          drawConstraint1(constraint, elefrom, eleto, graph);
      }
    });
  };

  const drawConstraintws = (con1: Constraint) => {
    let con = Constraint.copyConstraint(con1);
    project.value.addConstraint(con);
    drawConstraintOnGraph(con);
  };

  const update_deprecated = (pro: any) => {
    console.log('update', pro);
    wsdealId(pro.id);
    switch (pro.type) {
      case "add":
        wsadd(pro.shape, pro.new);
        break;
      case "delete":
        wsdelete(pro.shape, pro.old);
        break;
      case "change":
        wschange(pro.shape, pro.old, pro.new);
        break;
    }
  };

  const wsadd = (shape: string, new1: any) => {
    console.log("==========wsadd", shape, new1);
    switch (shape) {
      case "mac":
        drawMachinews(new1);
        break;
      case "pro":
        drawProblemDomainws(new1);
        break;
      case "req":
        drawRequirementws(new1);
        break;
      case "int":
        drawInterfacews(new1);
        break;
      case "ref":
        drawReferencews(new1);
        break;
      case "con":
        drawConstraintws(new1);
        break;
    }
  };

  const wsdelete = (shape: string, old: any) => {
    console.log('wsdelete', shape, old);
    switch (shape) {
      case "mac":
        deleteMachinews(old);
        break;
      case "pro":
        deleteProblemDomainws(old);
        break;
      case "req":
        deleteRequirementws(old);
        break;
      case "int":
        deleteInterfacews(old);
        break;
      case "ref":
        deleteReferencews(old);
        break;
      case "con":
        deleteConstraintws(old);
        break;
    }
  };

  const deleteMachinews = (old: Machine) => {
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    if (project.value.contextDiagram?.machine?.machine_shortName) {
        tmpProject.deleteRelatedLink(project.value.contextDiagram.machine.machine_shortName);
    }
    
    tmpProject.contextDiagram.machine = undefined as any;
    if (tmpProject.problemDiagram && tmpProject.problemDiagram.contextDiagram) {
        tmpProject.problemDiagram.contextDiagram.machine = undefined as any;
    }
    
    project.value = tmpProject;
    
    graphs.value.forEach(graph => {
        graph.getCells().forEach(element => {
            if (element.attr('root') && old.machine_shortName == element.attr('root').shortName) {
                graph.removeCells([element]);
            }
        });
    });
  };

  const deleteProblemDomainws = (pd1: ProblemDomain) => {
      const pd = ProblemDomain.copyProblemDomain(pd1);
      const name = pd.problemdomain_name;
      
      const tmpProject = new Project();
      tmpProject.initProject(project.value as Project);
      tmpProject.deleteRelatedLink(pd.problemdomain_shortname);
      
      const newList = tmpProject.contextDiagram.problemDomainList.filter(item => item.problemdomain_name !== name);
      tmpProject.contextDiagram.problemDomainList = newList;
      
      project.value = tmpProject;
      
      graphs.value.forEach(graph => {
          graph.getCells().forEach(element => {
             if (element.attr('root') && name == element.attr('root').title) {
                 graph.removeCells([element]);
             } 
          });
      });
  };

  const deleteRequirementws = (req1: Requirement) => {
      const req = Requirement.copyRequirement(req1);
      const name = req.requirement_context;
      
      const tmpProject = new Project();
      tmpProject.initProject(project.value as Project);
      tmpProject.deleteRelatedLink(name);
      
      const newList = tmpProject.problemDiagram.requirementList.filter(item => item.requirement_context !== name);
      tmpProject.problemDiagram.requirementList = newList;
      
      project.value = tmpProject;
      
      graphs.value.forEach(graph => {
          graph.getCells().forEach(element => {
             if (element.attr('root') && name == element.attr('root').title) {
                 graph.removeCells([element]);
             }
          });
      });
  };

  const deleteInterfacews = (int1: Interface) => {
      const int = Interface.copyInterface(int1);
      
      const tmpProject = new Project();
      tmpProject.initProject(project.value as Project);
      tmpProject.deleteInterface(int);
      project.value = tmpProject;
      
      deleteInterfaceOnGraph(int.interface_name);
  };

  const deleteInterfaceOnGraph = (name: string) => {
      graphs.value.forEach(graph => {
          graph.getCells().forEach(element => {
              if (element.attr('root') && name == element.attr('root').title) {
                  graph.removeCells([element]);
              }
          });
      });
  };

  const deleteReferencews = (ref1: Reference) => {
      const ref = Reference.copyReference(ref1);
      
      const tmpProject = new Project();
      tmpProject.initProject(project.value as Project);
      tmpProject.deleteReference(ref);
      project.value = tmpProject;
      
      deleteReferenceOnGraph(ref.reference_name);
  };

  const deleteReferenceOnGraph = (name: string) => {
      graphs.value.forEach(graph => {
          graph.getCells().forEach(element => {
              if (element.attr('root') && name == element.attr('root').title) {
                  graph.removeCells([element]);
              }
          });
      });
  };

  const deleteConstraintws = (con1: Constraint) => {
      const con = Constraint.copyConstraint(con1);
      
      const tmpProject = new Project();
      tmpProject.initProject(project.value as Project);
      tmpProject.deleteConstraint(con);
      project.value = tmpProject;
      
      deleteConstraintOnGraph(con.constraint_name);
  };

  const deleteConstraintOnGraph = (name: string) => {
      graphs.value.forEach(graph => {
          graph.getCells().forEach(element => {
              if (element.attr('root') && name == element.attr('root').title) {
                  graph.removeCells([element]);
              }
          });
      });
  };

  const changeRelatedLink = (oldName: string, newName: string) => {
    let i = project.value.problemDiagram.referenceList.length - 1;
    for (let reference of project.value.problemDiagram.referenceList) {
      if (reference.reference_from == oldName) {
        reference.reference_from = newName;
      } else if (reference.reference_to == oldName) {
        reference.reference_to = newName;
      }
    }
    for (let constraint of project.value.problemDiagram.constraintList) {
      if (constraint.constraint_from == oldName) {
        constraint.constraint_from = newName;
      } else if (constraint.constraint_to == oldName) {
        constraint.constraint_to = newName;
      }
    }
    for (let my_interface of project.value.contextDiagram.interfaceList) {
      if (my_interface.interface_from == oldName) {
        my_interface.interface_from = newName;
      } else if (my_interface.interface_to == oldName) {
        my_interface.interface_to = newName;
      }
    }
  };

  const changeMachineOnGraph = (name: string, shortName: string) => {
    graphs.value.forEach(graph => {
      graph.getCells().forEach(element => {
        if (element.attr("root") && element.attr("root").name == "machine") {
          element.attr({
            root: {
              title: name,
              name: "machine",
              shortName: shortName
            },
            label: { text: name + '\n(' + shortName + ')' }
          });
        }
      });
    });
  };

  const changeMachinews = (old: Machine, new1: Machine) => {
    changeRelatedLink(old.machine_shortName, new1.machine_shortName);
    changeMachineOnGraph(new1.machine_name, new1.machine_shortName);
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    tmpProject.changeMachine(new1.machine_name, new1.machine_shortName);
    project.value = tmpProject;
    return true;
  };

  const change2DesignDomain = (element: any) => {
    element.attr('r/refX', '-15');
    element.attr('r/refWidth', '20');
  };

  const change2GivenDomain = (element: any) => {
    element.attr('r/refX', '-5');
    element.attr('r/refWidth', '10');
  };

  const changeProblemDomainOnGraph = (domainEntity: ProblemDomain, domainElement: any) => {
    domainElement.attr({
      label: {
        text: domainEntity.problemdomain_name + '\n(' + domainEntity.problemdomain_shortname + ')',
      },
      root: {
        name: 'problemDomain',
        title: domainEntity.problemdomain_name,
        shortName: domainEntity.problemdomain_shortname,
      }
    });
    if (domainEntity.problemdomain_property === 'GivenDomain') {
      change2GivenDomain(domainElement);
    } else {
      change2DesignDomain(domainElement);
    }
  };

  const changeProblemDomainOnGraph1 = (old: ProblemDomain, new1: ProblemDomain) => {
    graphs.value.forEach(graph => {
      graph.getElements().forEach(element => {
        if (element.attr('root') && element.attr('root').title == old.problemdomain_name) {
          changeProblemDomainOnGraph(new1, element);
        }
      });
    });
  };

  const changeProblemDomainws = (old: ProblemDomain, new1: ProblemDomain) => {
    old = ProblemDomain.copyProblemDomain(old);
    new1 = ProblemDomain.copyProblemDomain(new1);
    changeRelatedLink(old.problemdomain_shortname, new1.problemdomain_shortname);
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    tmpProject.changeProblemDomain1(old, new1);
    project.value = tmpProject;
    changeProblemDomainOnGraph1(old, new1);
    return true;
  };

  const changeRequirementOnGraph = (element: any, name: string) => {
    element.attr({
      root: {
        title: name,
        shortName: name
      },
      label: { text: name }
    });
  };

  const changeRequirementOnGraph1 = (old: Requirement, new1: Requirement) => {
    graphs.value.forEach(graph => {
      graph.getElements().forEach(element => {
        if (element.attr('root') && element.attr('root').title == old.getName()) {
          changeRequirementOnGraph(element, new1.getName());
        }
      });
    });
  };

  const changeRequirementws = (old1: Requirement, new2: Requirement) => {
    let old = Requirement.copyRequirement(old1);
    let new1 = Requirement.copyRequirement(new2);
    changeRelatedLink(old.requirement_context, new1.requirement_context);
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    tmpProject.changeRequirement1(old, new1);
    project.value = tmpProject;
    changeRequirementOnGraph1(old, new1);
    return true;
  };

  const changeInterfacews = (old: Interface, new1: Interface) => {
    old = Interface.copyInterface(old);
    new1 = Interface.copyInterface(new1);
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    tmpProject.changeInterface(old, new1);
    project.value = tmpProject;
    return true;
  };

  const changeReferencews = (old: Reference, new1: Reference) => {
    old = Reference.copyReference(old);
    new1 = Reference.copyReference(new1);
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    tmpProject.changeReference(old, new1);
    project.value = tmpProject;
    return true;
  };

  const changeConstraintws = (old: Constraint, new1: Constraint) => {
    old = Constraint.copyConstraint(old);
    new1 = Constraint.copyConstraint(new1);
    const tmpProject = new Project();
    tmpProject.initProject(project.value as Project);
    tmpProject.changeConstraint(old, new1);
    project.value = tmpProject;
    return true;
  };

  const wschange = (shape: string, old: any, new1: any) => {
    switch (shape) {
      case "mac":
        changeMachinews(old, new1);
        break;
      case "pro":
        changeProblemDomainws(old, new1);
        break;
      case "req":
        changeRequirementws(old, new1);
        break;
      case "int":
        changeInterfacews(old, new1);
        break;
      case "ref":
        changeReferencews(old, new1);
        break;
      case "con":
        changeConstraintws(old, new1);
        break;
    }
  };

  const update = (pro: any) => {
    console.log('update:', pro);
    wsdealId(pro.id);
    switch (pro.type) {
      case "add":
        wsadd(pro.shape, pro.new);
        break;
      case "delete":
        wsdelete(pro.shape, pro.old);
        break;
      case "change":
        wschange(pro.shape, pro.old, pro.new);
        break;
    }
  };

  const openWebSocket = () => {
    ws.value = new WebSocket('ws://localhost:7086/webSocket');
    ws.value.onopen = () => {
      console.log('client: ws connection is open');
    };
    ws.value.onmessage = (e) => {
      console.log("=====????????=======")
      const pro = JSON.parse(e.data);
      console.log(pro);
      console.log("============")
      update(pro);
    };
    ws.value.onerror = (e) => {
      console.log('=================================error================================', e);
    };
    ws.value.onclose = (e) => {
      console.log("=================================close===============================", e);
      // Reconnect logic
      setTimeout(openWebSocket, 1000);
    };
  };

  const register = (title: string) => {
    console.log("-------------------register-----:", title);
    if (project.value.title != undefined) {
      unregister(projectAddress.value);
    }
    const message = {
      "type": "register",
      "title": title,
      "id": messageId.value
    };
    projectAddress.value = title;
    console.log("============send message=============");
    console.log(message);
    ws.value?.send(JSON.stringify(message));
  };

  const unregister = (title: string) => {
    const message = {
      "type": "unregister",
      "title": title,
      "id": messageId.value
    };
    console.log("================send message=================")
    console.log(message);
    ws.value?.send(JSON.stringify(message));
  };
  
  const change = (type: string, shape: string, old: any, new1: any) => {
    let ver = version.value;
    if (ver == undefined)
      ver = "undefined";
    console.log("this.messageId=", messageId.value);
    const message = {
      "type": type,
      "title": projectAddress.value,
      "version": ver,
      "id": messageId.value,
      "shape": shape,
      "old": old,
      "new": new1
    };
    console.log("============send message============");
    console.log(message);
    ws.value?.send(JSON.stringify(message));
  };

  const deleteSelection = () => {
    if (!clickedPaper.value) return;
    const graph = clickedPaper.value.model;
    const paperId = clickedPaper.value.id;
    const sgName = paperId.substring(0, paperId.length - 1);

    // Delete Link
    if (clickedToolView.value) {
      const link = clickedToolView.value.model;
      const title = link.attr('root/title');
      if (title) {
        // Remove from Project Store
        const sgList = project.value.scenarioGraphList;
        if (sgList) {
          const sg = sgList.find(s => s.title === sgName);
          if (sg && sg.lineList) {
             sg.lineList = sg.lineList.filter(l => (l.line_type + l.line_no) !== title);
          }
        }
      }
      link.remove();
      clickedToolView.value = null;
      return;
    }

    // Delete Element
    if (clickedSceElement.value) {
      const element = clickedSceElement.value;
      const title = element.attr('root/title');
      if (title) {
         // Remove from Project Store
         const sgList = project.value.scenarioGraphList;
         if (sgList) {
            const sg = sgList.find(s => s.title === sgName);
            if (sg) {
                // Try removing from intNodeList
                if (sg.intNodeList) {
                    sg.intNodeList = sg.intNodeList.filter(n => (n.node_type + n.node_no) !== title);
                }
                // Try removing from ctrlNodeList
                if (sg.ctrlNodeList) {
                    sg.ctrlNodeList = sg.ctrlNodeList.filter(n => (n.node_type + n.node_no) !== title);
                }
            }
         }
      }
      element.remove();
      clickedSceElement.value = null;
    }
  };

  // Force all element/link views in a paper to re-render.
  // Needed after a v-show hidden container becomes visible: JointJS can't measure
  // SVG text while hidden (getBBox returns 0), so views must be updated on reveal.
  const forceUpdatePaper = (paper: joint.dia.Paper) => {
    if (!paper || !paper.model) return;
    const graph = paper.model;
    graph.getElements().forEach(el => {
      const view = paper.findViewByModel(el);
      if (view) (view as any).update();
    });
    graph.getLinks().forEach(link => {
      const view = paper.findViewByModel(link);
      if (view) (view as any).update();
    });
  };

  return {
    editingMachine,
    editingDomain,
    editingInterface,
    editingReference,
    editingConstraint,
    editingRequirement,
    graphs,
    papers,
    project,
    clickedPaper,
    deleteSelection, // Export this
    initPapers,
    drawMachine,
    drawMachine2,
    drawProblemDomain,
    drawProblemDomains,
    drawRequirement,
    drawRequirements,
    drawInterface,
    drawInterfaces,
    drawConstraints,
    drawReferences,
    drawCtrlNode,
    drawLine,
    drawNewSenarioGraph,
    drawFullSenarioGraph,
    drawNewSpd,
    drawSubProblemDiagram,
    drawProblemDiagram1,
    drawContextDiagram1,
    IntCondition_clone,
    drawCtrlNode_clone,
    openWebSocket,
    register,
    forceUpdatePaper
  };

}
