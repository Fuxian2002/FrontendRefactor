import { defineStore } from 'pinia';
import { ref } from 'vue';
import { type Elements, type Node, isNode, isEdge, MarkerType } from '@vue-flow/core';
import axios from 'axios';
import { showDialog } from 'vant';
import { CONFIG } from '@/config';

export const useLspStore = defineStore('lsp', () => {
  const isConnected = ref(false);
  const isTextConnected = ref(false);
  const ws = ref<WebSocket | null>(null);
  const textWs = ref<WebSocket | null>(null);
  const elements = ref<Elements>([]);
  const projectVersion = ref<string>('0');
  const projectAddress = ref<string>('');
  const projectData = ref<any>(null);

  // Counters for ID generation (simplified)
  let idCounter = 0;
  const machineCounter = ref(0);
  const domainCounter = ref(0);
  const reqCounter = ref(0);
  const interfaceCounter = ref(0);
  const constraintCounter = ref(0);
  const systemCounter = ref(0);
  const extCounter = ref(0);
  const intentCounter = ref(0);
  const refCounter = ref(0);

  function connect() {
    connectDiagram();
    connectText();
  }

  function connectDiagram() {
    if (ws.value && (ws.value.readyState === WebSocket.OPEN || ws.value.readyState === WebSocket.CONNECTING)) {
      return;
    }

    // Connect to Backend WebSocket for Diagram Sync
    ws.value = new WebSocket(`${CONFIG.backend.wsUrl}${CONFIG.backend.endpoints.webSocket}`);

    ws.value.onopen = () => {
      console.log('Diagram LSP WebSocket Connected');
      isConnected.value = true;
      // Start heartbeat
      setInterval(() => {
        if (ws.value?.readyState === WebSocket.OPEN) {
          // Heartbeat if needed
        }
      }, 30000);
    };

    ws.value.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data);
        handleMessage(msg);
      } catch (e) {
        console.error('Failed to parse Diagram LSP message', e);
      }
    };

    ws.value.onclose = (event) => {
      console.log(`Diagram LSP WebSocket Closed (Code: ${event.code}, Reason: ${event.reason}). Reconnecting in 3s...`);
      isConnected.value = false;
      setTimeout(() => {
        connectDiagram();
      }, 3000);
    };
  }

  function connectText() {
    if (textWs.value && (textWs.value.readyState === WebSocket.OPEN || textWs.value.readyState === WebSocket.CONNECTING)) {
      return;
    }

    // Connect to Backend WebSocket for Text Sync
    textWs.value = new WebSocket(`${CONFIG.backend.wsUrl}${CONFIG.backend.endpoints.textLsp}`);

    textWs.value.onopen = () => {
      console.log('Text LSP WebSocket Connected');
      isTextConnected.value = true;
    };

    textWs.value.onmessage = (event) => {
      try {
        const msg = JSON.parse(event.data);
        console.log('Received Text LSP Message:', msg);
        // Handle text messages if needed
      } catch (e) {
        console.error('Failed to parse Text LSP message', e);
      }
    };

    textWs.value.onclose = (event) => {
      console.log(`Text LSP WebSocket Closed (Code: ${event.code}, Reason: ${event.reason}). Reconnecting in 3s...`);
      isTextConnected.value = false;
      setTimeout(() => {
        connectText();
      }, 3000);
    };
  }

  function waitForConnection(type: 'diagram' | 'text' = 'diagram', timeout = 5000): Promise<boolean> {
    return new Promise((resolve) => {
      const socket = type === 'diagram' ? ws.value : textWs.value;
      const connectFunc = type === 'diagram' ? connectDiagram : connectText;

      if (socket?.readyState === WebSocket.OPEN) {
        resolve(true);
        return;
      }

      if (!socket || socket.readyState === WebSocket.CLOSED) {
        connectFunc();
      }

      const start = Date.now();
      const interval = setInterval(() => {
        const currentSocket = type === 'diagram' ? ws.value : textWs.value;
        if (currentSocket?.readyState === WebSocket.OPEN) {
          clearInterval(interval);
          resolve(true);
        } else if (Date.now() - start > timeout) {
          clearInterval(interval);
          resolve(false);
        }
      }, 100);
    });
  }

  function handleMessage(msg: any) {
    console.log('Received LSP Message:', msg);
    if (msg.method === 'Diagram/didChange') {
      const params = msg.params;
      if (params && params.contentChanges) {
        params.contentChanges.forEach((change: any) => {
           applyChange(change);
        });
      }
    } else {
        // Handle full project update if message has no method (initial load)
        // DrawGraphService: if(message.method == undefined || message.method == "") that.update(pro);
        if (!msg.method) {
            // Assume it's a project object
            // We would parse this and populate elements
            // For now, let's just log it
            console.log('Full project update received', msg);
        }
    }
  }

  function applyChange(change: any) {
      console.log(change);
      // Apply change to local elements
      // This requires mapping 'shapeType' and 'changeType' to Vue Flow updates
      // For now, we trust our local optimistic updates or implement this later
      // The backend might send back the exact node we added, so we should update it
      // or prevent duplication.
  }

  function getUserName() {
    const match = document.cookie.match(/username=([^;]+)/);
    return match ? match[1] : 'test';
  }

  async function createNewProject(projectName: string): Promise<boolean> {
    const username = getUserName();
    
    // 1. Check if project exists
    try {
      const res = await axios.get<string[]>(`${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.searchProject}?username=${username}`);
      const projects = res.data;
      if (projects && projects.includes(projectName)) {
        alert(`${projectName} already exists!`);
        return false;
      }
    } catch (e) {
      console.error('Error searching projects', e);
      // Proceeding despite error for now, or you could return false
    }

    // 2. Initialize Project Structure
    // Default System Node
    const defaultSystem = {
        system_name: 'system',
        system_shortName: 'S1',
        system_x: 400,
        system_y: 300,
        system_h: 60,
        system_w: 120
    };
    
    const defaultMachine = {
        machine_name: 'machine',
        machine_shortName: 'M1',
        machine_x: 0,
        machine_y: 0,
        machine_w: 200,
        machine_h: 100
    };

    const project = {
      title: projectName,
      contextDiagram: { machine: defaultMachine, problemDomainList: [], interfaceList: [], constraintList: [], referenceList: [] },
      problemDiagram: { requirementList: [], constraintList: [], referenceList: [] },
      intentDiagram: { system: defaultSystem, externalEntityList: [], intentList: [] },
      scenarioGraphList: [],
      ontologyEntityList: []
    };

    // 3. Register via WebSocket
    const version = "undefined";
    const uri = `${username}_${projectName}_${version}`;
    
    projectAddress.value = projectName;
    projectVersion.value = version;
    
    // Initialize elements with the default system node
    elements.value = [
        {
            id: `system-${Date.now()}`,
            type: 'system',
            position: { x: defaultSystem.system_x, y: defaultSystem.system_y },
            data: {
                label: defaultSystem.system_name,
                type: 'system',
                ...defaultSystem
            },
            label: defaultSystem.system_name,
            style: { 
                width: '120px', 
                height: '60px', 
                border: '1px dashed black', 
                padding: '0', 
                borderRadius: '5px', 
                backgroundColor: 'transparent' 
            }
        }
    ];

    const diagram = {
      username: username,
      uri: uri,
      project: project
    };

    const message = {
      id: Date.now(),
      method: "Diagram/didOpen",
      params: { diagram: diagram }
    };

    const connected = await waitForConnection('diagram');
    if (connected && ws.value) {
      ws.value.send(JSON.stringify(message));

      // Also register Text (pf)
      const textMessage = {
        id: Date.now() + 1,
        method: "TextDocument/didOpen",
        params: {
          textDocument: {
            uri: uri,
            text: `problem: #${projectName}#\n`,
            version: version
          }
        }
      };

      try {
        const textConnected = await waitForConnection('text');
        if (textConnected && textWs.value) {
          textWs.value.send(JSON.stringify(textMessage));
        } else {
          console.warn("Text WebSocket not connected - Text LSP features may be unavailable");
        }
      } catch (e) {
        console.warn("Error connecting to Text WebSocket", e);
      }

      return true;
    } else {
      console.error("WebSocket not connected after waiting");
      alert("WebSocket connection failed. Please check backend server.");
      return false;
    }
  }

  async function getProjectList(): Promise<string[]> {
    const username = getUserName();
    try {
      const res = await axios.get<string[]>(`${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.searchProject}?username=${username}`);
      return res.data;
    } catch (e) {
      console.error('Error fetching project list', e);
      return [];
    }
  }

  async function openProject(projectName: string, version: string = "undefined"): Promise<boolean> {
    const username = getUserName();
    const uri = `${username}_${projectName}_${version}`;
    
    projectAddress.value = projectName;
    projectVersion.value = version;
    
    // Clear existing elements locally
    elements.value = [];

    // In the original app, open logic sends Diagram/didOpen if it's a fresh open?
    // Or maybe we should just register?
    // Angular app: register2 -> Diagram/didOpen
    // But register2 takes a 'pro' object. If we open an existing project, we might need to fetch it first?
    // Actually, Angular app: 
    // 1. dg_service.getProject(project, version) -> fetches project JSON
    // 2. dg_service.register2(..., pro) -> sends Diagram/didOpen with project data
    
    // So we need to fetch the project first.
    try {
      // API: /file/getProject/{projectAddress}/{version}?username={username}
      // Note: version in URL might need handling if "undefined"
      const url = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.getProject}/${projectName}/${version}?username=${username}`;
      const res = await axios.get(url);
      projectData.value = res.data;

      // ?????????????????????????????????????????
      if (projectData.value) {
          // ?????????
          elements.value = [];
          
          // ???? contextDiagram (Context Diagram)
          if (projectData.value.contextDiagram) {
              const cd = projectData.value.contextDiagram;
              
              // Machine
              if (cd.machine) {
                  const m = cd.machine;
                  elements.value.push({
                      id: `machine-${Date.now()}`,
                      type: 'machine',
                      position: { x: m.machine_x, y: m.machine_y },
                      data: { ...m, label: m.machine_name, type: 'machine' },
                      label: m.machine_name,
                      style: { 
                          backgroundColor: '#ffffff', 
                          border: '1px solid black', 
                          width: `${m.machine_w || 200}px`, 
                          height: `${m.machine_h || 100}px`, 
                          padding: '0',
                          zIndex: 100
                      },
                      zIndex: 100
                  });
              }
              
              // Problem Domains
              if (cd.problemDomainList) {
                  cd.problemDomainList.forEach((pd: any) => {
                      elements.value.push({
                          id: `pd-${pd.problemdomain_no}-${Date.now()}`,
                          type: 'problemDomain',
                          position: { x: pd.problemdomain_x, y: pd.problemdomain_y },
                          data: { ...pd, label: pd.problemdomain_name, type: 'problemDomain' },
                          label: pd.problemdomain_name,
                          style: { 
                              backgroundColor: '#ffffff', 
                              border: '1px solid black', 
                              width: `${pd.problemdomain_w || 140}px`, 
                              height: `${pd.problemdomain_h || 60}px`, 
                              padding: '0' 
                          }
                      });
                  });
              }
              
              // Interfaces (Edges) - ????????????????????????????????????? ShortName ???? ID (????)
              // ??????????????????????? ID????????????????
              // ?????????????????? ShortName -> NodeID ?????
              // ?????????????????????????????? ID ????????
              // ????????????????????? Node?????????? ShortName??
          }

          // ???? intentDiagram (Intent Diagram)
          if (projectData.intentDiagram) {
              const id = projectData.intentDiagram;
              
              // System
              if (id.system) {
                  const s = id.system;
                  elements.value.push({
                      id: `system-${Date.now()}`,
                      type: 'system',
                      position: { x: s.system_x, y: s.system_y },
                      data: { ...s, label: s.system_name, type: 'system' },
                      label: s.system_name,
                      style: { 
                           backgroundColor: 'transparent', 
                           border: '1px dashed black', 
                           width: `${s.system_w || 120}px`, 
                           height: `${s.system_h || 60}px`, 
                           padding: '0',
                           zIndex: -1 
                      },
                      zIndex: -1
                  });
              }
              
              // External Entities, Intents...
          }

          // ???? problemDiagram (Problem Diagram)
          if (projectData.value.problemDiagram) {
              const pd = projectData.value.problemDiagram;
              
              // Requirements
              if (pd.requirementList) {
                  pd.requirementList.forEach((req: any) => {
                      elements.value.push({
                          id: `req-${req.requirement_no}-${Date.now()}`,
                          type: 'requirement',
                          position: { x: req.requirement_x, y: req.requirement_y },
                          data: { ...req, label: req.requirement_context, type: 'requirement' },
                          label: req.requirement_context,
                          style: { 
                              backgroundColor: 'transparent', 
                              border: 'none', 
                              width: `${req.requirement_w || 120}px`, 
                              height: `${req.requirement_h || 60}px`, 
                              padding: '0' 
                          }
                      });
                  });
              }
          }
          
          // ??????? NodeMap (ShortName -> ID) ????????
          const nodeMap = new Map<string, string>();
          elements.value.forEach((el) => {
               if (isNode(el) && el.data) {
                   let shortName = '';
                   if (el.data.machine_shortName) shortName = el.data.machine_shortName;
                   else if (el.data.problemdomain_shortname) shortName = el.data.problemdomain_shortname;
                   else if (el.data.system_shortName) shortName = el.data.system_shortName;
                   // For loading, we might need to map BOTH shortname and context to ID?
                   // Backend saves with whatever we sent. If we sent context as 'from', we need to match context.
                   // Let's map both to be safe.
                   if (el.data.requirement_shortname) nodeMap.set(el.data.requirement_shortname, el.id);
                   if (el.data.requirement_context) nodeMap.set(el.data.requirement_context, el.id);
                   
                   if (el.data.requirement_shortname) shortName = el.data.requirement_shortname;
                   else if (el.data.externalentity_shortname) shortName = el.data.externalentity_shortname;
                   else if (el.data.intent_shortname) shortName = el.data.intent_shortname;
                   
                   if (shortName && !el.data.requirement_shortname) nodeMap.set(shortName, el.id);
               }
          });
          
          // ????? (Interfaces, Constraints, References)
          // 2. Interfaces
          if (projectData.value.contextDiagram && projectData.value.contextDiagram.interfaceList) {
              projectData.value.contextDiagram.interfaceList.forEach((intf: any) => {
                  const sourceId = nodeMap.get(intf.interface_from);
                  const targetId = nodeMap.get(intf.interface_to);
                  
                  if (sourceId && targetId) {
                      // Reconstruct interface_phenomenon_list string if it exists as array but not string
                      let pheListStr = intf.interface_phenomenon_list;
                      if (!pheListStr && intf.phenomenonList && Array.isArray(intf.phenomenonList)) {
                          pheListStr = intf.phenomenonList.map((p: any) => {
                              const from = p.phenomenon_from || '';
                              const name = p.phenomenon_name || '';
                              const type = p.phenomenon_type || 'event';
                              return `${from}! ${name} ${type}`;
                          }).join('\n');
                      }

                      // ??? Handle ??? (???????)
                      elements.value.push({
                          id: `e-${sourceId}-${targetId}-${Date.now()}-${Math.random()}`,
                          source: sourceId,
                          target: targetId,
                          sourceHandle: 'right', // ???????????
                          targetHandle: 'left',
                          type: 'step',
                          label: intf.interface_name,
                          style: { stroke: 'black', strokeWidth: 1.5 },
                          labelStyle: { fill: 'black', fontWeight: 700, fontSize: '12px' },
                          labelBgStyle: { fill: 'white', fillOpacity: 0.8, rx: 4, ry: 4 }, 
                          data: { ...intf, interface_phenomenon_list: pheListStr, type: 'interface' }
                      });
                  }
              });
          }
          
          // 2. Constraints
          if (projectData.value.problemDiagram && projectData.value.problemDiagram.constraintList) {
               projectData.value.problemDiagram.constraintList.forEach((con: any) => {
                  // Constraint: From Domain -> To Requirement (Logic) or vice versa?
                  // In data: constraint_from = Domain, constraint_to = Requirement
                  // Visual Arrow: Requirement -> Domain.
                  // So Source = Req (to), Target = Domain (from)
                  const sourceId = nodeMap.get(con.constraint_to);
                  const targetId = nodeMap.get(con.constraint_from);
                  
                  if (sourceId && targetId) {
                      // Reconstruct constraint_phenomenon_list string if it exists as array but not string
                      let pheListStr = con.constraint_phenomenon_list;
                      if (!pheListStr && con.phenomenonList && Array.isArray(con.phenomenonList)) {
                          pheListStr = con.phenomenonList.map((p: any) => {
                              const from = p.phenomenon_from || '';
                              const name = p.phenomenon_name || '';
                              const type = p.phenomenon_type || 'event';
                              return `${from}! ${name} ${type}`;
                          }).join('\n');
                      }

                      elements.value.push({
                          id: `c-${sourceId}-${targetId}-${Date.now()}-${Math.random()}`,
                          source: sourceId,
                          target: targetId,
                          sourceHandle: 'bottom', 
                          targetHandle: 'top',
                          type: 'default',
                          label: con.constraint_name,
                          style: { stroke: 'black', strokeWidth: 1.5, strokeDasharray: '5,5' },
                          markerEnd: MarkerType.ArrowClosed,
                          labelStyle: { fill: 'black', fontWeight: 700, fontSize: '12px' },
                          labelBgStyle: { fill: 'white', fillOpacity: 0.8, rx: 4, ry: 4 }, 
                          data: { ...con, constraint_phenomenon_list: pheListStr, type: 'constraint' }
                      });
                  }
               });
          }
          
          // 3. References
          if (projectData.value.problemDiagram && projectData.value.problemDiagram.referenceList) {
               projectData.value.problemDiagram.referenceList.forEach((ref: any) => {
                  const sourceId = nodeMap.get(ref.reference_to);
                  const targetId = nodeMap.get(ref.reference_from);
                  
                  if (sourceId && targetId) {
                      // Reconstruct reference_phenomenon_list string if it exists as array but not string
                      let pheListStr = ref.reference_phenomenon_list;
                      if (!pheListStr && ref.phenomenonList && Array.isArray(ref.phenomenonList)) {
                          pheListStr = ref.phenomenonList.map((p: any) => {
                              const from = p.phenomenon_from || '';
                              const name = p.phenomenon_name || '';
                              const type = p.phenomenon_type || 'event';
                              return `${from}! ${name} ${type}`;
                          }).join('\n');
                      }

                      elements.value.push({
                          id: `r-${sourceId}-${targetId}-${Date.now()}-${Math.random()}`,
                          source: sourceId,
                          target: targetId,
                          sourceHandle: 'bottom', 
                          targetHandle: 'top',
                          type: 'default',
                          label: ref.reference_name,
                          style: { stroke: 'black', strokeWidth: 1.5, strokeDasharray: '5,5' },
                          markerEnd: MarkerType.ArrowClosed,
                          labelStyle: { fill: 'black', fontWeight: 700, fontSize: '12px' },
                          labelBgStyle: { fill: 'white', fillOpacity: 0.8, rx: 4, ry: 4 }, 
                          data: { ...ref, reference_phenomenon_list: pheListStr, type: 'reference' }
                      });
                  }
               });
          }
      }

      const diagram = {
        username: username,
        uri: uri,
        project: projectData.value
      };

      const message = {
        id: Date.now(),
        method: "Diagram/didOpen",
        params: { diagram: diagram }
      };

      const connected = await waitForConnection('diagram');
      if (connected && ws.value) {
        ws.value.send(JSON.stringify(message));
        
        // Also register Text (pf)
        // Need to fetch text content? Angular: textService.getNotNullPf -> register
        // API: /file/getNotNullPf/{projectAddress}/{version}?username={username}
        const textUrl = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.getNotNullPf}/${projectName}/${version}?username=${username}`;
        try {
          const textRes = await axios.get(textUrl, { responseType: 'text' });
          const textContent = textRes.data;

          const textMessage = {
            id: Date.now() + 1,
            method: "TextDocument/didOpen",
            params: {
              textDocument: {
                uri: uri,
                text: textContent,
                version: version
              }
            }
          };
          
          const textConnected = await waitForConnection('text');
          if (textConnected && textWs.value) {
            textWs.value.send(JSON.stringify(textMessage));
          }
        } catch (textError) {
          console.warn('Failed to load text content', textError);
        }

        return true;
      } else {
        console.error("WebSocket not connected after waiting");
        alert("WebSocket connection failed. Please check backend server.");
        return false;
      }

    } catch (e) {
      console.error('Error opening project', e);
      alert('Failed to open project');
      return false;
    }
  }

  async function saveProject(): Promise<boolean> {
    if (!projectAddress.value) {
      alert("No project opened to save");
      return false;
    }
    const username = getUserName();
    // We need to construct the project object from the current state (elements).
    // This is complex because we need to map Vue Flow elements back to the Angular Project structure.
    // For now, let's assume we can just save the project metadata or what we have.
    // However, the backend expects a Project object in the body.
    
    // Ideally, we should maintain the Project object state in the store and update it as we edit.
    // Since we only have 'elements' (Vue Flow nodes), we might need to rely on the backend's state 
    // if the backend is keeping track of the project via LSP.
    // But the save API is explicit: saveProject(address, project).
    
    // For this migration, maybe we can fetch the project from backend (to get current structure),
    // update it with our changes (if any we can map), and send it back.
    // Or, if we are using LSP, maybe 'save' is just telling backend to persist what it has?
    // The `saveProject` API seems to take the project object.
    
    // Let's try to fetch the latest project first to respect the structure
    try {
        const urlGet = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.getProject}/${projectAddress.value}/${projectVersion.value}?username=${username}`;
        let currentProject: any = null;
        try {
             const resGet = await axios.get(urlGet);
             currentProject = resGet.data;
        } catch (err) {
            console.warn('Failed to get project from backend, creating new structure', err);
        }

        if (!currentProject) {
            currentProject = projectData.value || {};
        }
        currentProject = sanitizeProject(currentProject);
        if (!currentProject) {
            currentProject = sanitizeProject({});
        }
        // Ensure title is set, as backend requires it
        if (!currentProject.title) {
            currentProject.title = projectAddress.value;
        }

        // Sync Elements to Project Structure (Context Diagram)
        // 1. Machine
        const machineNode = elements.value.find(n => n.type === 'machine' || (n.data && n.data.type === 'machine')) as any;
        if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
        if (machineNode) {
            currentProject.contextDiagram.machine = {
                machine_name: machineNode.data.machine_name || 'machine',
                machine_shortName: machineNode.data.machine_shortName || 'M1',
                machine_x: machineNode.position.x,
                machine_y: machineNode.position.y,
                machine_w: machineNode.data.machine_w || 200,
                machine_h: machineNode.data.machine_h || 100
            };
        } else {
             // Fix 500 Error: Backend requires machine to be present for ContextDiagram saving
             currentProject.contextDiagram.machine = {
                machine_name: 'machine',
                machine_shortName: 'M1',
                machine_x: 0,
                machine_y: 0,
                machine_w: 200,
                machine_h: 100
            };
        }

        // 2. System
        const systemNode = elements.value.find(n => n.type === 'system' || (n.data && n.data.type === 'system')) as any;
        if (systemNode) {
           if (!currentProject.intentDiagram) currentProject.intentDiagram = {};
           currentProject.intentDiagram.system = {
                system_name: systemNode.data.system_name || 'system',
                system_shortName: systemNode.data.system_shortName || 'S1',
                system_x: systemNode.position.x,
                system_y: systemNode.position.y,
                system_h: systemNode.data.system_h || 60,
                system_w: systemNode.data.system_w || 120
           };
        }

        // 3. Problem Domains
        const pdNodes = elements.value.filter(n => isNode(n) && (n.type === 'problemDomain' || (n.data && n.data.type === 'problemDomain'))) as any[];
        if (pdNodes.length > 0) {
            if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
            currentProject.contextDiagram.problemDomainList = pdNodes.map((n: any) => ({
                problemdomain_no: n.data.problemdomain_no || 0,
                problemdomain_name: n.data.problemdomain_name || 'PD',
                problemdomain_shortname: n.data.problemdomain_shortname || 'PD1',
                problemdomain_type: n.data.problemdomain_type || 'Causal',
                problemdomain_property: n.data.problemdomain_property || 'GivenDomain',
                problemdomain_x: n.position.x,
                problemdomain_y: n.position.y,
                problemdomain_w: n.data.problemdomain_w || 100,
                problemdomain_h: n.data.problemdomain_h || 50,
                phes: [] 
            }));
        } else {
            if (currentProject.contextDiagram) currentProject.contextDiagram.problemDomainList = [];
        }

        // 4. Interfaces
        const nodeMap = new Map<string, string>();
        elements.value.forEach((el: any) => {
            if (isNode(el)) {
                let shortName = '';
                if (el.data) {
                    if (el.data.machine_shortName) shortName = el.data.machine_shortName;
                    else if (el.data.problemdomain_shortname) shortName = el.data.problemdomain_shortname;
                    else if (el.data.system_shortName) shortName = el.data.system_shortName;
                    // Use Context for Requirement to satisfy backend validation
                    else if (el.data.requirement_context) shortName = el.data.requirement_context;
                    else if (el.data.requirement_shortname) shortName = el.data.requirement_shortname;
                    else if (el.data.externalentity_shortname) shortName = el.data.externalentity_shortname;
                    else if (el.data.intent_shortname) shortName = el.data.intent_shortname;
                }
                
                if (shortName) nodeMap.set(el.id, shortName);
            }
        });

        const edges = elements.value.filter(el => isEdge(el)) as any[];
        const interfaces = edges.filter(e => e.data && e.data.type === 'interface');
        
        if (interfaces.length > 0) {
            if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
            currentProject.contextDiagram.interfaceList = interfaces.map((edge: any) => {
                const fromShort = nodeMap.get(edge.source) || edge.source || "";
                const toShort = nodeMap.get(edge.target) || edge.target || "";
                return {
                    interface_no: edge.data.interface_no || 0,
                    interface_name: edge.label || "int",
                    interface_description: edge.label || "int",
                    interface_from: fromShort,
                    interface_to: toShort,
                    phenomenonList: parsePhenomenonList(edge.data.interface_phenomenon_list, false, fromShort)
                };
            });
        } else {
             if (currentProject.contextDiagram) currentProject.contextDiagram.interfaceList = [];
        }

        // Sync Elements to Project Structure (Problem Diagram)
        // 5. Requirements
        const reqNodes = elements.value.filter(n => isNode(n) && (n.type === 'requirement' || (n.data && n.data.type === 'requirement'))) as any[];
        if (reqNodes.length > 0) {
             if (!currentProject.problemDiagram) currentProject.problemDiagram = {};
             currentProject.problemDiagram.requirementList = reqNodes.map((n: any) => ({
                 requirement_no: n.data.requirement_no || 0,
                 requirement_context: n.data.requirement_context || 'req',
                 requirement_shortname: n.data.requirement_shortname || 'REQ1',
                 requirement_x: n.position.x,
                 requirement_y: n.position.y,
                 requirement_w: n.data.requirement_w || 120,
                 requirement_h: n.data.requirement_h || 60,
                 phes: []
             }));
        } else {
             if (currentProject.problemDiagram) currentProject.problemDiagram.requirementList = [];
        }

        // 6. Constraints
        const constraints = edges.filter(e => e.data && e.data.type === 'constraint');
        if (constraints.length > 0) {
            if (!currentProject.problemDiagram) currentProject.problemDiagram = {};
            currentProject.problemDiagram.constraintList = constraints.map((edge: any) => {
                 const reqShort = nodeMap.get(edge.source) || edge.source || "";
                 const domainShort = nodeMap.get(edge.target) || edge.target || "";
                 
                 const sourceNode = elements.value.find(el => el.id === edge.source);
                 const targetNode = elements.value.find(el => el.id === edge.target);
                 
                 let fromShort = "";
                 let toShort = "";
                 
                 const isReq = (n: any) => n && (n.type === 'requirement' || (n.data && n.data.type === 'requirement'));
                 
                 if (isReq(sourceNode) && !isReq(targetNode)) {
                     fromShort = domainShort;
                     toShort = reqShort;
                 } else if (isReq(targetNode) && !isReq(sourceNode)) {
                     fromShort = domainShort;
                     toShort = reqShort;
                 } else {
                      fromShort = edge.data.constraint_from || domainShort;
                      toShort = edge.data.constraint_to || reqShort;
                 }
                 
                 // Prioritize user override for constraint_from/to if set
                 if (edge.data.constraint_from) fromShort = edge.data.constraint_from;
                 if (edge.data.constraint_to) toShort = edge.data.constraint_to;

                 return {
                     constraint_no: edge.data.constraint_no || 0,
                     constraint_name: edge.label || "con",
                     constraint_description: edge.label || "con",
                     constraint_from: fromShort,
                     constraint_to: toShort,
                      phenomenonList: parsePhenomenonList(edge.data.constraint_phenomenon_list, true, fromShort)
                  };
            });
        } else {
             if (currentProject.problemDiagram) currentProject.problemDiagram.constraintList = [];
        }

    // 7. References
    const references = edges.filter(e => e.data && e.data.type === 'reference');
    if (references.length > 0) {
        if (!currentProject.problemDiagram) currentProject.problemDiagram = {};
        currentProject.problemDiagram.referenceList = references.map((edge: any) => {
             const reqShort = nodeMap.get(edge.source) || edge.source || "";
             const domainShort = nodeMap.get(edge.target) || edge.target || "";
             
             const sourceNode = elements.value.find(el => el.id === edge.source);
             const targetNode = elements.value.find(el => el.id === edge.target);
             
             let fromShort = "";
             let toShort = "";
             
             const isReq = (n: any) => n && (n.type === 'requirement' || (n.data && n.data.type === 'requirement'));
             
             if (isReq(sourceNode) && !isReq(targetNode)) {
                 fromShort = domainShort;
                 toShort = reqShort;
             } else if (isReq(targetNode) && !isReq(sourceNode)) {
                 fromShort = domainShort;
                 toShort = reqShort;
             } else {
                  fromShort = edge.data.reference_from || domainShort;
                  toShort = edge.data.reference_to || reqShort;
             }

             return {
                 reference_no: edge.data.reference_no || 0,
                 reference_name: edge.label || "ref",
                 reference_description: edge.label || "ref",
                 reference_from: fromShort,
                 reference_to: toShort,
                  phenomenonList: parsePhenomenonList(edge.data.reference_phenomenon_list, true, fromShort)
              };
        });
    } else {
         if (currentProject.problemDiagram) currentProject.problemDiagram.referenceList = [];
    }
        
        // Deep Sanitize before sending to backend to avoid 500 errors
        currentProject = sanitizeForBackend(currentProject);
        
        const urlSave = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.saveProject}/${projectAddress.value}?username=${username}`;
        await axios.post(urlSave, currentProject, {
            headers: { 'Content-Type': 'application/json' }
        });
        alert('Project saved successfully');
        return true;
    } catch (e) {
        console.error('Error saving project', e);
        alert('Failed to save project');
        return false;
    }
  }

  async function downloadProject(): Promise<void> {
    if (!projectAddress.value) {
        alert("No project opened to download");
        return;
    }
    const username = getUserName();
    const url = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.download}/${projectAddress.value}/${username}`;
    
    try {
        const response = await axios.post(url, {}, {
            responseType: 'blob', // Important for file download
            headers: { 'Content-Type': 'application/json' }
        });
        
        // Create a link to download the blob
        const urlBlob = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = urlBlob;
        link.setAttribute('download', `${projectAddress.value}.zip`); // Assuming zip
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    } catch (e) {
        console.error('Error downloading project', e);
        alert('Failed to download project');
    }
  }

  function addNodeToProject(type: string, x: number, y: number, customData?: { name?: string, shortName?: string, domainType?: string, property?: string }) {
    if (!ws.value || ws.value.readyState !== WebSocket.OPEN) {
      console.warn('WebSocket not connected');
    }

    // Determine shape parameters based on type
    let newShape: any = null;
    let shapeType = mapTypeToShapeType(type);

    if (type === 'machine') {
      const name = 'machine';
      const shortName = 'M1';
      newShape = {
        machine_name: name,
        machine_shortName: shortName,
        machine_x: x,
        machine_y: y,
        machine_w: 200,
        machine_h: 100
      };
    } else if (type === 'problemDomain') {
      const no = ++domainCounter.value;
      const name = customData?.name || `Problem domain`;
      const shortName = customData?.shortName || `D${no}`;
      newShape = {
        problemdomain_no: no,
        problemdomain_name: name,
        problemdomain_shortname: shortName,
        problemdomain_type: customData?.domainType || 'Clock', // Default to Clock as requested
        problemdomain_property: customData?.property || 'GivenDomain', // Default
        problemdomain_x: x,
        problemdomain_y: y,
        problemdomain_w: 140,
        problemdomain_h: 60
      };
    } else if (type === 'requirement') {
      const no = ++reqCounter.value;
      const name = `requirement${no}`;
      const shortName = `REQ${no}`;
      newShape = {
        requirement_no: no,
        requirement_context: name,
        requirement_shortname: shortName,
        requirement_x: x,
        requirement_y: y,
        requirement_w: 120,
        requirement_h: 60
      };
    } else if (type === 'system') {
      const name = customData?.name || `System${++systemCounter.value}`;
      const shortName = customData?.shortName || `S${systemCounter.value}`;
      if (!customData?.name) {
          // If using auto-generated, we already incremented.
          // If using custom, we might not want to increment, but keeping it simple is fine.
          // Actually if custom is provided, we should probably NOT increment if we want to be strict,
          // but for now let's just use the provided values.
      }
      
      newShape = {
        system_name: name,
        system_shortName: shortName,
        system_x: x,
        system_y: y,
        system_w: 100,
        system_h: 50
      };
    } else if (type === 'extentity') {
      const no = ++extCounter.value;
      const name = `ExtEntity${no}`;
      const shortName = `E${no}`;
      newShape = {
        externalentity_no: no,
        externalentity_name: name,
        externalentity_shortname: shortName,
        externalentity_x: x,
        externalentity_y: y,
        externalentity_w: 100,
        externalentity_h: 50
      };
    } else if (type === 'intent') {
      const no = ++intentCounter.value;
      const name = `Intent${no}`;
      const shortName = `I${no}`;
      newShape = {
        intent_no: no,
        intent_context: name,
        intent_shortname: shortName,
        intent_x: x,
        intent_y: y,
        intent_w: 100,
        intent_h: 50
      };
    } else {
        console.warn(`Type ${type} not fully supported for add yet.`);
        return;
    }

    const id = `${type}-${Date.now()}`;
    const label = newShape.machine_name || 
                  newShape.problemdomain_name || 
                  newShape.requirement_context || 
                  newShape.system_name || 
                  newShape.externalentity_name || 
                  newShape.intent_context ||
                  type;

    const newNode: Node = {
      id,
      type: 'default', // Map internal types to Vue Flow types
      position: { x, y },
      data: { label, type, ...newShape }, // Pass all shape properties to data
      label: label,
      style: { width: '100px', height: '50px', border: '1px solid #777', padding: '10px', borderRadius: '5px', background: 'white' }
    };
    
    // Customize style based on type
    if (type === 'machine') {
       newNode.style = { 
           ...newNode.style, 
           backgroundColor: '#ffffff', 
           border: '1px solid black', 
           width: '250px', 
           height: '120px', 
           padding: '0',
           zIndex: 100
       };
       newNode.type = 'machine';
       newNode.zIndex = 100;
    } else if (type === 'problemDomain') {
       newNode.style = { ...newNode.style, backgroundColor: '#ffffff', border: '1px solid black', width: '140px', height: '60px', padding: '0' };
       newNode.type = 'problemDomain';
    } else if (type === 'requirement') {
       newNode.style = { ...newNode.style, backgroundColor: 'transparent', border: 'none', width: '120px', height: '60px', padding: '0' };
       newNode.type = 'requirement';
    } else if (type === 'system') {
       newNode.style = { ...newNode.style, backgroundColor: 'transparent', border: '1px dashed black', width: '120px', height: '60px', padding: '0' };
       newNode.type = 'system'; // Use custom node component
    } else if (type === 'extentity') {
       newNode.style = { ...newNode.style, backgroundColor: '#ffffff', border: '1px solid black' };
    } else if (type === 'intent') {
       newNode.style = { ...newNode.style, backgroundColor: '#f0ffff', border: '1px dashed black', borderRadius: '50%' };
    } else if (type === 'interface') {
       newNode.style = { ...newNode.style, height: '10px', backgroundColor: 'black', border: 'none', borderRadius: '0' };
       // Visual representation as a line (simplified)
    } else if (type === 'reference') {
       newNode.style = { ...newNode.style, height: '10px', backgroundColor: 'gray', border: 'none', borderRadius: '0', borderStyle: 'dashed' };
    } else if (type === 'constraint') {
       newNode.style = { ...newNode.style, height: '10px', backgroundColor: 'red', border: 'none', borderRadius: '0' };
    }

    // Optimistic update
    elements.value.push(newNode);

    // Send to Backend
    const changeEvent = {
        shapeType: shapeType,
        changeType: 'add',
        newShape: newShape,
        newProject: {} 
    };

    const message = {
        jsonrpc: '2.0',
        id: Date.now(),
        method: 'Diagram/didChange',
        params: {
            diagram: {
                uri: projectAddress.value,
                version: projectVersion.value
            },
            contentChanges: [changeEvent]
        }
    };

    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        ws.value.send(JSON.stringify(message));
    }
  }

  function mapTypeToShapeType(type: string): string {
      // "mac/pro/req/sys/ext/tas/int/ref/con"
      const map: Record<string, string> = {
          'machine': 'mac',
          'problemDomain': 'pro',
          'requirement': 'req',
          'interface': 'int',
          'constraint': 'con',
          'reference': 'ref',
          'system': 'sys',
          'extentity': 'ext',
          'intent': 'tas',
      };
      return map[type] || 'mac';
  }

  function sanitizeProject(project: any) {
    if (!project) return null;
    
    // Ensure diagrams exist
    if (!project.intentDiagram) project.intentDiagram = { system: null, externalEntityList: [], intentList: [], interfaceList: [], constraintList: [], referenceList: [] };
    if (!project.contextDiagram) project.contextDiagram = { machine: null, problemDomainList: [], interfaceList: [] };
    if (!project.problemDiagram) project.problemDiagram = { requirementList: [], constraintList: [], referenceList: [], contextDiagram: project.contextDiagram };
    
    // Ensure lists in diagrams are arrays
    const id = project.intentDiagram;
    if (!id.externalEntityList) id.externalEntityList = [];
    if (!id.intentList) id.intentList = [];
    if (!id.constraintList) id.constraintList = [];
    if (!id.interfaceList) id.interfaceList = [];
    if (!id.referenceList) id.referenceList = [];
    
    const cd = project.contextDiagram;
    if (!cd.problemDomainList) cd.problemDomainList = [];
    if (!cd.interfaceList) cd.interfaceList = [];
    
    const pd = project.problemDiagram;
    if (!pd.requirementList) pd.requirementList = [];
    if (!pd.constraintList) pd.constraintList = [];
    if (!pd.referenceList) pd.referenceList = [];
    // Ensure back-reference
    pd.contextDiagram = cd;
    
    // Ensure root lists
    if (!project.scenarioGraphList) project.scenarioGraphList = [];
    if (!project.subProblemDiagramList) project.subProblemDiagramList = [];
    if (!project.traceList) project.traceList = [];
    if (!project.dataDependenceList) project.dataDependenceList = [];
    if (!project.controlDependenceList) project.controlDependenceList = [];
    
    return project;
  }

  function sanitizeForBackend(obj: any): any {
       if (obj === null || obj === undefined) {
           return null;
       }
       if (Array.isArray(obj)) {
           return obj
               .map(item => sanitizeForBackend(item))
               .filter(item => item !== null && item !== undefined);
       }
       if (typeof obj === 'object') {
           const newObj: any = {};
           for (const key in obj) {
               if (Object.prototype.hasOwnProperty.call(obj, key)) {
                   // 1. Ensure lists are arrays
                   if (key.endsWith('List')) {
                        if (obj[key] === null || obj[key] === undefined) {
                            newObj[key] = [];
                        } else {
                            newObj[key] = sanitizeForBackend(obj[key]);
                        }
                   } 
                   // 2. Ensure string fields are not null (to avoid replaceAll NPE in backend)
                   else if (
                       key === 'title' || // Explicitly handle title
                       key.toLowerCase().endsWith('_name') || 
                       key.toLowerCase().endsWith('_shortname') || 
                       key.toLowerCase().endsWith('_context') || 
                       key.toLowerCase().endsWith('_description') ||
                       key.toLowerCase().endsWith('_from') || 
                       key.toLowerCase().endsWith('_to') ||
                       key.toLowerCase().endsWith('_type') ||
                       key.toLowerCase().endsWith('_property') ||
                       key.toLowerCase().endsWith('_constraint') // Handle phenomenon_constraint
                   ) {
                       if (obj[key] === null || obj[key] === undefined) {
                           newObj[key] = "";
                       } else {
                           newObj[key] = String(obj[key]); // Ensure it's a string
                       }
                   }
                   // 3. Handle potential numbers to prevent nulls if backend expects primitives
                   else if (
                       key.toLowerCase().endsWith('_no') || 
                       key.toLowerCase().endsWith('_x') || 
                       key.toLowerCase().endsWith('_y') || 
                       key.toLowerCase().endsWith('_w') || 
                       key.toLowerCase().endsWith('_h') ||
                       key.toLowerCase().endsWith('_x1') || 
                       key.toLowerCase().endsWith('_y1') || 
                       key.toLowerCase().endsWith('_x2') || 
                       key.toLowerCase().endsWith('_y2') ||
                       key.toLowerCase().endsWith('_requirement') // Handle phenomenon_requirement
                   ) {
                       if (obj[key] === null || obj[key] === undefined) {
                           newObj[key] = 0;
                       } else {
                           newObj[key] = obj[key];
                       }
                   }
                   // 4. Recurse for other objects
                   else {
                       newObj[key] = sanitizeForBackend(obj[key]);
                   }
               }
           }
           return newObj;
       }
       return obj;
   }

  function deepSanitize(obj: any): any {
      if (obj === null || obj === undefined) {
          return null;
      }
      if (Array.isArray(obj)) {
          return obj.map(item => deepSanitize(item));
      }
      if (typeof obj === 'object') {
          const newObj: any = {};
          for (const key in obj) {
              if (Object.prototype.hasOwnProperty.call(obj, key)) {
                  // Ensure specific lists are arrays if they are missing
                  if (key.endsWith('List') && (obj[key] === null || obj[key] === undefined)) {
                      newObj[key] = [];
                  } else {
                      newObj[key] = deepSanitize(obj[key]);
                  }
              }
          }
          return newObj;
      }
      return obj;
  }

  const parsePhenomenonList = (listStr: string | undefined, isRequirement: boolean, defaultFrom: string = '') => {
      if (!listStr) return [];
      const lines = listStr.split('\n').filter(s => s.trim() !== '');
      return lines.map((line, index) => {
          let pheName = line;
          let pheFrom = defaultFrom;
          let pheType = 'event';
          
          // Handle "from!name type" syntax
          if (line.includes('!')) {
              const parts = line.split('!');
              if (parts.length >= 2) {
                  pheFrom = parts[0].trim();
                  const rest = parts.slice(1).join('!').trim();
                  
                  // Try to find type at the end
                  const lastSpaceIndex = rest.lastIndexOf(' ');
                  if (lastSpaceIndex !== -1) {
                      const possibleType = rest.substring(lastSpaceIndex + 1);
                      if (['event', 'state', 'value', 'instruction', 'signal'].includes(possibleType)) {
                          pheType = possibleType;
                          pheName = rest.substring(0, lastSpaceIndex).trim();
                      } else {
                          pheName = rest;
                      }
                  } else {
                      pheName = rest;
                  }
              }
          }

          const basePhe = {
              phenomenon_no: index + 1,
              phenomenon_name: pheName,
              phenomenon_shortname: pheName,
              phenomenon_type: pheType,
              phenomenon_from: pheFrom,
              phenomenon_to: ''
          };
          
          if (isRequirement) {
              return {
                  ...basePhe,
                  phenomenon_requirement: 0,
                  phenomenon_constraint: ''
              };
          }
          return basePhe;
      });
  };

  async function checkContext(): Promise<boolean> {
      if (!projectAddress.value) return false;
      const username = getUserName();
      
      try {
          // 1. Try to get project from backend file (might be outdated or null if not saved)
          let currentProject = null;
          try {
            const urlGet = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.getProject}/${projectAddress.value}/${projectVersion.value}?username=${username}`;
            const resGet = await axios.get(urlGet);
            currentProject = resGet.data;
          } catch (err) {
            console.warn('Failed to get project from file, using local state', err);
          }

          // 2. Fallback to local projectData if file fetch failed
          if (!currentProject) {
              currentProject = projectData.value;
          }

          // 3. Sanitize (initialize structure)
          currentProject = sanitizeForBackend(sanitizeProject(currentProject));
          if (!currentProject) {
              // If still null, create a minimal skeleton
              currentProject = sanitizeForBackend(sanitizeProject({})); 
              currentProject.title = projectAddress.value;
          }

          // 4. Sync critical info from elements to currentProject (especially Machine for Step 2)
           // Because backend check relies on the Project object structure, not visual elements.
           const machineNode = elements.value.find(n => n.type === 'machine' || (n.data && n.data.type === 'machine')) as any;
           if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
           if (machineNode) {
               currentProject.contextDiagram.machine = {
                   machine_name: machineNode.data.machine_name || 'machine',
                   machine_shortName: machineNode.data.machine_shortName || 'M1',
                   machine_x: machineNode.position.x,
                   machine_y: machineNode.position.y,
                   machine_w: machineNode.data.machine_w || 200,
                   machine_h: machineNode.data.machine_h || 100
               };
           } else {
               // Ensure machine exists even if not drawn, to prevent 500 error in backend saveContextDiagram
               // Backend checks: if (machine != null && machine.getShortname() != null)
               // But sanitization might have left it null.
               // Let's provide a default hidden machine if missing.
               currentProject.contextDiagram.machine = {
                   machine_name: 'machine',
                   machine_shortName: 'M1',
                   machine_x: 0,
                   machine_y: 0,
                   machine_w: 200,
                   machine_h: 100
               };
           }
           
           // Also sync System Node (in Intent Diagram)
           const systemNode = elements.value.find(n => n.type === 'system' || (n.data && n.data.type === 'system')) as any;
           if (systemNode) {
              if (!currentProject.intentDiagram) currentProject.intentDiagram = {};
              currentProject.intentDiagram.system = {
                   system_name: systemNode.data.system_name || 'system',
                   system_shortName: systemNode.data.system_shortName || 'S1',
                   system_x: systemNode.position.x,
                   system_y: systemNode.position.y,
                   system_h: systemNode.data.system_h || 60,
                   system_w: systemNode.data.system_w || 120
              };
           }

           // 3. Sync Problem Domains (Context Diagram)
           const pdNodes = elements.value.filter(n => isNode(n) && (n.type === 'problemDomain' || (n.data && n.data.type === 'problemDomain'))) as any[];
           if (pdNodes.length > 0) {
               if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
               currentProject.contextDiagram.problemDomainList = pdNodes.map((n: any) => ({
                   problemdomain_no: n.data.problemdomain_no || 0,
                   problemdomain_name: n.data.problemdomain_name || 'PD',
                   problemdomain_shortname: n.data.problemdomain_shortname || 'PD1',
                   problemdomain_type: n.data.problemdomain_type || 'Causal',
                   problemdomain_property: n.data.problemdomain_property || 'GivenDomain',
                   problemdomain_x: n.position.x,
                   problemdomain_y: n.position.y,
                   problemdomain_w: n.data.problemdomain_w || 100,
                   problemdomain_h: n.data.problemdomain_h || 50,
                   phes: [] 
               }));
           } else {
               // Ensure list is initialized even if empty, but maybe not overwrite if we want to keep what's on server?
               // But we are syncing FROM canvas TO check payload. So canvas is truth.
               if (currentProject.contextDiagram) currentProject.contextDiagram.problemDomainList = [];
           }

           // 4. Sync Interfaces (Edges)
           // First build a map of ID -> ShortName to resolve edge source/target
           const nodeMap = new Map<string, string>();
           elements.value.forEach((el: any) => {
               if (isNode(el)) {
                   let shortName = '';
                   if (el.data) {
                        if (el.data.machine_shortName) shortName = el.data.machine_shortName;
                        else if (el.data.problemdomain_shortname) shortName = el.data.problemdomain_shortname;
                        else if (el.data.system_shortName) shortName = el.data.system_shortName;
                        // Use Context for Requirement
                        else if (el.data.requirement_context) shortName = el.data.requirement_context;
                        else if (el.data.requirement_shortname) shortName = el.data.requirement_shortname;
                        else if (el.data.externalentity_shortname) shortName = el.data.externalentity_shortname;
                        else if (el.data.intent_shortname) shortName = el.data.intent_shortname;
                   }
                   
                   if (shortName) nodeMap.set(el.id, shortName);
               }
           });

           const edges = elements.value.filter(el => isEdge(el) && el.data && el.data.type === 'interface') as any[];
           if (edges.length > 0) {
               if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
               currentProject.contextDiagram.interfaceList = edges.map((edge: any, index: number) => {
                   const fromShort = nodeMap.get(edge.source) || edge.source;
                   const toShort = nodeMap.get(edge.target) || edge.target;
                   
                   return {
                       interface_no: index + 1,
                       interface_name: edge.label || `int${index+1}`,
                       interface_description: edge.label || `int${index+1}`,
                       interface_from: fromShort,
                       interface_to: toShort,
                       phenomenonList: parsePhenomenonList(edge.data.interface_phenomenon_list, false, fromShort),
                       interface_x1: 0,
                       interface_y1: 0,
                       interface_x2: 0,
                       interface_y2: 0,
                       isintent: false
                   };
               });
           } else {
               if (currentProject.contextDiagram) currentProject.contextDiagram.interfaceList = [];
           }


          const urlCheck = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.checkCorrectContext}`;
          console.log('Checking context with project:', currentProject);
          const resCheck = await axios.post(urlCheck, currentProject, {
            headers: {
                'Content-Type': 'application/json'
            }
          });
          const errors = resCheck.data;
          
          const hasErrors = errors.some((e: any) => e.errorList && e.errorList.length > 0);
          
          if (hasErrors) {
              let message = '';
              errors.forEach((e: any) => {
                  if (e.errorList && e.errorList.length > 0) {
                      message += `${e.errorList.join('\n')}\n`;
                  }
              });
              
              showDialog({
                  title: '检查结果',
                  message: message,
                  confirmButtonText: '确定'
              });
              return false;
          } else {
               showDialog({
                  title: '检查结果',
                  message: 'The diagram is correct.',
                  confirmButtonText: '确定'
              });
              return true;
          }
      } catch (e) {
          console.error('Check context failed', e);
          showDialog({
              title: 'Error',
              message: 'Failed to perform check.',
          });
          return false;
      }
  }

  function initStep2() {
      const systemNode = elements.value.find(n => isNode(n) && n.data?.type === 'system') as Node;
      if (systemNode) {
          // Check if machine node already exists to avoid duplicates
          const existingMachine = elements.value.find(n => isNode(n) && n.data?.type === 'machine');
          if (existingMachine) {
              return;
          }

          // Resize System Node
          systemNode.position = { x: 100, y: 100 }; // Move closer to origin
          systemNode.style = {
              ...systemNode.style,
            width: '600px',
            height: '500px',
            backgroundColor: 'rgba(255, 255, 255, 0)', // Transparent
            border: '2px dashed black',
            zIndex: -1
        };
        systemNode.zIndex = -1;
        
        // Update local data
        const shapeType = mapTypeToShapeType('system');
        const updatedShape = {
            ...systemNode.data,
            system_w: 600,
            system_h: 500,
            system_x: 100,
            system_y: 100
        };
        systemNode.data = updatedShape;

        // Send update to backend
        const changeEvent = {
            shapeType: shapeType,
            changeType: 'change', 
            newShape: updatedShape,
            newProject: {}
        };
        
        const message = {
            jsonrpc: '2.0',
            id: Date.now(),
            method: 'Diagram/didChange',
            params: {
                diagram: { uri: projectAddress.value, version: projectVersion.value },
                contentChanges: [changeEvent]
            }
        };
        if (ws.value && ws.value.readyState === WebSocket.OPEN) {
            ws.value.send(JSON.stringify(message));
        }

        // Add Machine Node
        // Position it inside System Node (Centered)
        // System: 100, 100, 600x500. Center: 400, 350
        // Machine: 250x120. Center: 400, 350 -> TopLeft: 275, 290
        const machineX = 275;
        const machineY = 290;
        addNodeToProject('machine', machineX, machineY);
    }
  }

  function addInterface(sourceNode: any, targetNode: any) {
    const no = ++interfaceCounter.value;
    const name = `Interface${no}`;
    
    // Determine From/To names (prefer ShortName, then Name, then Label)
    const getShortName = (node: any) => {
        if (!node.data) return node.label || node.id;
        return node.data.machine_shortName || 
               node.data.problemdomain_shortname || 
               node.data.system_shortName || 
               node.data.externalentity_shortname || 
               node.data.intent_shortname || 
               node.label || node.id;
    };

    // Calculate Handles based on geometry
    const getCenter = (node: any) => {
        // Fallback dimensions if not ready
        const w = node.dimensions?.width || (node.type === 'system' || node.type === 'machine' ? 200 : 140);
        const h = node.dimensions?.height || (node.type === 'system' || node.type === 'machine' ? 120 : 60);
        return {
            x: node.position.x + w / 2,
            y: node.position.y + h / 2
        };
    };

    const c1 = getCenter(sourceNode);
    const c2 = getCenter(targetNode);
    const dx = c2.x - c1.x;
    const dy = c2.y - c1.y;

    let finalSource = sourceNode;
    let finalTarget = targetNode;
    let sourceHandle = '';
    let targetHandle = '';

    // Logic to determine handles and swap to ensure Flow is Left->Right or Top->Bottom
    // Our Handles: Left/Top = Target, Right/Bottom = Source.
    if (Math.abs(dx) > Math.abs(dy)) {
        // Horizontal relationship
        if (dx > 0) {
            // Target is to the Right: Source(Right) -> Target(Left)
            sourceHandle = 'right';
            targetHandle = 'left';
        } else {
            // Target is to the Left: Target(Right) -> Source(Left)
            // Swap nodes to match handle direction
            finalSource = targetNode;
            finalTarget = sourceNode;
            sourceHandle = 'right';
            targetHandle = 'left';
        }
    } else {
        // Vertical relationship
        if (dy > 0) {
            // Target is Below: Source(Bottom) -> Target(Top)
            sourceHandle = 'bottom';
            targetHandle = 'top';
        } else {
            // Target is Above: Target(Bottom) -> Source(Top)
            // Swap nodes
            finalSource = targetNode;
            finalTarget = sourceNode;
            sourceHandle = 'bottom';
            targetHandle = 'top';
        }
    }

    const fromName = getShortName(finalSource);
    const toName = getShortName(finalTarget);

    const newShape = {
        interface_no: no,
        interface_name: name,
        interface_description: "",
        interface_from: fromName,
        interface_to: toName,
        interface_phenomenon: "",
        interface_type: "state",
        interface_phenomenon_list: "",
        interface_x1: 0,
        interface_y1: 0,
        interface_x2: 0,
        interface_y2: 0,
        isintent: false
    };

    const newEdge = {
        id: `e-${finalSource.id}-${finalTarget.id}-${Date.now()}`,
        source: finalSource.id,
        target: finalTarget.id,
        sourceHandle: sourceHandle,
        targetHandle: targetHandle,
        type: 'step', // Orthogonal line (Manhattan routing)
        label: name,
        style: { stroke: 'black', strokeWidth: 1.5 },
        labelStyle: { fill: 'black', fontWeight: 700, fontSize: '12px' },
        labelBgStyle: { fill: 'white', fillOpacity: 0.8, rx: 4, ry: 4 }, 
        data: { ...newShape, type: 'interface' },
    };

    elements.value.push(newEdge);

    // Notify Backend
    const changeEvent = {
        shapeType: 'int',
        changeType: 'add',
        newShape: newShape,
        projectPath: projectAddress.value
    };

    const message = {
        jsonrpc: '2.0',
        id: Date.now(),
        method: 'Diagram/didChange',
        params: {
            diagram: { uri: projectAddress.value, version: projectVersion.value },
            contentChanges: [changeEvent]
        }
    };

    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        ws.value.send(JSON.stringify(message));
    } else {
        console.warn('WebSocket not connected, interface added locally only.');
    }
  }

  function addConstraint(sourceNode: any, targetNode: any) {
    const no = ++constraintCounter.value;
    const name = `Constraint${no}`; // User can rename to 'd', 'e' etc.
    
    // Determine From/To names
    const getShortName = (node: any) => {
        if (!node.data) return node.label || node.id;
        return node.data.machine_shortName || 
               node.data.problemdomain_shortname || 
               node.data.system_shortName || 
               node.data.externalentity_shortname || 
               node.data.intent_shortname || 
               node.label || node.id;
    };

    const getCenter = (node: any) => {
        const w = node.dimensions?.width || (node.type === 'system' || node.type === 'machine' ? 200 : 140);
        const h = node.dimensions?.height || (node.type === 'system' || node.type === 'machine' ? 120 : 60);
        return {
            x: node.position.x + w / 2,
            y: node.position.y + h / 2
        };
    };

    const c1 = getCenter(sourceNode);
    const c2 = getCenter(targetNode);
    const dx = c2.x - c1.x;
    const dy = c2.y - c1.y;

    let finalSource = sourceNode;
    let finalTarget = targetNode;
    let sourceHandle = '';
    let targetHandle = '';

    // Logic to determine handles (reuse interface logic or simplify)
    // For dashed constraint lines, straight connection might be better, but user showed arrows.
    // Let's use same logic as interface for handle points to ensure it attaches to sides.
    if (Math.abs(dx) > Math.abs(dy)) {
        if (dx > 0) {
            sourceHandle = 'right';
            targetHandle = 'left';
        } else {
            finalSource = targetNode;
            finalTarget = sourceNode;
            sourceHandle = 'right';
            targetHandle = 'left';
        }
    } else {
        if (dy > 0) {
            sourceHandle = 'bottom';
            targetHandle = 'top';
        } else {
            finalSource = targetNode;
            finalTarget = sourceNode;
            sourceHandle = 'bottom';
            targetHandle = 'top';
        }
    }

    // Determine Logic Source/Target based on semantics (Requirement -> Domain)
    // The user screenshot clearly shows:
    // Arrow: Requirement ---> Domain (D1)
    // So: Requirement should be Source (Initiator), Domain should be Target (Receiver).
    // WAIT: User Update (2025-01-18):
    // "Initiator should be D1 (Domain), which is the node pointed to by the arrow".
    // "Receiver default empty".
    // So: Arrow points to Domain (D1).
    // Connection: Requirement -> Domain. (Source: Req, Target: Domain).
    // Arrow Head: At Target (Domain).
    // Initiator Field: Domain Name.
    // Receiver Field: Empty.

    let edgeSource = finalSource;
    let edgeTarget = finalTarget;
    let edgeSourceHandle = sourceHandle;
    let edgeTargetHandle = targetHandle;

    const isReq = (n: any) => n.type === 'requirement';
    
    // If 'finalSource' is the Requirement, we want it to be the Source of the Edge
    // so that the arrow (at Target) points to Domain.
    
    if (isReq(finalSource) && !isReq(finalTarget)) {
        // Current: Requirement -> Domain.
        // Correct direction for Arrow -> Domain.
        // edgeSource = finalSource (Req);
        // edgeTarget = finalTarget (Domain);
    } else if (isReq(finalTarget) && !isReq(finalSource)) {
        // Current: Domain -> Requirement.
        // Swap to: Requirement -> Domain.
        edgeSource = finalTarget;
        edgeTarget = finalSource;
        edgeSourceHandle = targetHandle; // Use the handle on the Req
        edgeTargetHandle = sourceHandle; // Use the handle on the Domain
    }
    
    // If both or neither are requirements, keep geometric order (or click order? keeping geometric for clean lines)

    const fromName = getShortName(edgeTarget); // Initiator is the Arrow Target (Domain)
    const toName = ""; // Receiver default empty

    const newShape = {
        constraint_no: no,
        constraint_name: name,
        constraint_from: fromName,
        constraint_to: toName,
        constraint_phenomenon: "",
        constraint_type: "instruction", // Default type
        constraint_phenomenon_list: ""
    };

    const newEdge = {
        id: `c-${edgeSource.id}-${edgeTarget.id}-${Date.now()}`,
        source: edgeSource.id,
        target: edgeTarget.id,
        sourceHandle: edgeSourceHandle,
        targetHandle: edgeTargetHandle,
        type: 'default', // Straight line
        label: name,
        animated: false,
        style: { stroke: 'black', strokeWidth: 1.5, strokeDasharray: '5,5' }, // Dashed line
        markerEnd: MarkerType.ArrowClosed,
        labelStyle: { fill: 'black', fontWeight: 700, fontSize: '12px' },
        labelBgStyle: { fill: 'white', fillOpacity: 0.8, rx: 4, ry: 4 }, 
        data: { ...newShape, type: 'constraint' },
    };

    elements.value.push(newEdge);

    // Notify Backend (Assuming 'con' shapeType or similar, sticking to 'add' logic)
    // TODO: Verify shapeType for constraint. Using 'con' as placeholder or 'int' if treated as interface.
    // Given the separate editor, likely a separate concept.
    const changeEvent = {
        shapeType: 'con', // Guessing 'con' for Constraint
        changeType: 'add',
        newShape: newShape,
        projectPath: projectAddress.value
    };

    const message = {
        jsonrpc: '2.0',
        id: Date.now(),
        method: 'Diagram/didChange',
        params: {
            diagram: { uri: projectAddress.value, version: projectVersion.value },
            contentChanges: [changeEvent]
        }
    };

    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        ws.value.send(JSON.stringify(message));
    }
  }

  function addReference(sourceNode: any, targetNode: any) {
    const no = ++refCounter.value;
    const name = `Reference${no}`;
    
    // Determine From/To names
    const getShortName = (node: any) => {
        if (!node.data) return node.label || node.id;
        return node.data.machine_shortName || 
               node.data.problemdomain_shortname || 
               node.data.system_shortName || 
               node.data.externalentity_shortname || 
               node.data.intent_shortname || 
               node.data.requirement_shortname ||
               node.label || node.id;
    };

    const getCenter = (node: any) => {
        const w = node.dimensions?.width || (node.type === 'system' || node.type === 'machine' ? 200 : 140);
        const h = node.dimensions?.height || (node.type === 'system' || node.type === 'machine' ? 120 : 60);
        return {
            x: node.position.x + w / 2,
            y: node.position.y + h / 2
        };
    };

    const c1 = getCenter(sourceNode);
    const c2 = getCenter(targetNode);
    const dx = c2.x - c1.x;
    const dy = c2.y - c1.y;

    let finalSource = sourceNode;
    let finalTarget = targetNode;
    let sourceHandle = '';
    let targetHandle = '';

    if (Math.abs(dx) > Math.abs(dy)) {
        if (dx > 0) {
            sourceHandle = 'right';
            targetHandle = 'left';
        } else {
            finalSource = targetNode;
            finalTarget = sourceNode;
            sourceHandle = 'right';
            targetHandle = 'left';
        }
    } else {
        if (dy > 0) {
            sourceHandle = 'bottom';
            targetHandle = 'top';
        } else {
            finalSource = targetNode;
            finalTarget = sourceNode;
            sourceHandle = 'bottom';
            targetHandle = 'top';
        }
    }

    // Logic: Requirement -> Domain (Reference)
    let edgeSource = finalSource;
    let edgeTarget = finalTarget;
    let edgeSourceHandle = sourceHandle;
    let edgeTargetHandle = targetHandle;

    const isReq = (n: any) => n.type === 'requirement';
    
    if (isReq(finalSource) && !isReq(finalTarget)) {
         // Correct
    } else if (isReq(finalTarget) && !isReq(finalSource)) {
        edgeSource = finalTarget;
        edgeTarget = finalSource;
        edgeSourceHandle = targetHandle;
        edgeTargetHandle = sourceHandle;
    }

    const fromName = getShortName(edgeTarget); // Initiator is the Arrow Target (Domain)
    const toName = ""; // Receiver default empty

    const newShape = {
        reference_no: no,
        reference_name: name,
        reference_from: fromName,
        reference_to: toName,
        reference_phenomenon: "",
        reference_type: "instruction",
        reference_phenomenon_list: ""
    };

    const newEdge = {
        id: `r-${edgeSource.id}-${edgeTarget.id}-${Date.now()}`,
        source: edgeSource.id,
        target: edgeTarget.id,
        sourceHandle: edgeSourceHandle,
        targetHandle: edgeTargetHandle,
        type: 'default', 
        label: name,
        animated: false,
        style: { stroke: 'black', strokeWidth: 1.5, strokeDasharray: '5,5' }, 
        markerEnd: MarkerType.ArrowClosed,
        labelStyle: { fill: 'black', fontWeight: 700, fontSize: '12px' },
        labelBgStyle: { fill: 'white', fillOpacity: 0.8, rx: 4, ry: 4 }, 
        data: { ...newShape, type: 'reference' },
    };

    elements.value.push(newEdge);

    const changeEvent = {
        shapeType: 'ref',
        changeType: 'add',
        newShape: newShape,
        projectPath: projectAddress.value
    };

    const message = {
        jsonrpc: '2.0',
        id: Date.now(),
        method: 'Diagram/didChange',
        params: {
            diagram: { uri: projectAddress.value, version: projectVersion.value },
            contentChanges: [changeEvent]
        }
    };

    if (ws.value && ws.value.readyState === WebSocket.OPEN) {
        ws.value.send(JSON.stringify(message));
    }
  }

  function updateShape(node: any) {
      if (!node || !node.data) return;
      
      let shapeType = 'unknown';
      // Determine shapeType based on node type
      if (node.type === 'system') shapeType = mapTypeToShapeType('system');
      else if (node.type === 'machine') shapeType = mapTypeToShapeType('machine');
      else if (node.type === 'problemDomain') shapeType = mapTypeToShapeType('problemDomain');
      else if (node.type === 'requirement') shapeType = mapTypeToShapeType('requirement');
      else if (node.data.type === 'constraint') shapeType = 'con';
      else if (node.data.type === 'reference') shapeType = 'ref';
      else if (node.data.type === 'interface') shapeType = 'int';
      
      const changeEvent = {
          shapeType: shapeType,
          changeType: 'change',
          newShape: node.data,
          projectPath: projectAddress.value
      };

      const message = {
          jsonrpc: '2.0',
          id: Date.now(),
          method: 'Diagram/didChange',
          params: {
              diagram: { uri: projectAddress.value, version: projectVersion.value },
              contentChanges: [changeEvent]
          }
      };

      if (ws.value && ws.value.readyState === WebSocket.OPEN) {
          ws.value.send(JSON.stringify(message));
      }
  }

  async function checkProblem(): Promise<boolean> {
    if (!projectAddress.value) return false;
    const username = getUserName();
    
    try {
        let currentProject = null;
        try {
          const urlGet = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.getProject}/${projectAddress.value}/${projectVersion.value}?username=${username}`;
          const resGet = await axios.get(urlGet);
          currentProject = resGet.data;
        } catch (err) {
          console.warn('Failed to get project from file, using local state', err);
        }

        if (!currentProject) {
            currentProject = projectData.value;
        }

        currentProject = sanitizeForBackend(sanitizeProject(currentProject));
        if (!currentProject) {
            currentProject = sanitizeForBackend(sanitizeProject({})); 
            currentProject.title = projectAddress.value;
        }

        // Sync Problem Diagram Elements
        // 1. Requirements
        const reqNodes = elements.value.filter(n => isNode(n) && (n.type === 'requirement' || (n.data && n.data.type === 'requirement'))) as any[];
        if (reqNodes.length > 0) {
             if (!currentProject.problemDiagram) currentProject.problemDiagram = {};
             currentProject.problemDiagram.requirementList = reqNodes.map((n: any) => ({
                 requirement_no: n.data.requirement_no || 0,
                 requirement_context: n.data.requirement_context || 'req',
                 requirement_shortname: n.data.requirement_shortname || 'REQ1',
                 requirement_x: n.position.x,
                 requirement_y: n.position.y,
                 requirement_w: n.data.requirement_w || 120,
                 requirement_h: n.data.requirement_h || 60,
                 phes: []
             }));
        } else {
             if (currentProject.problemDiagram) currentProject.problemDiagram.requirementList = [];
        }

        // 2. Constraints and References (Edges)
        const edges = elements.value.filter(el => isEdge(el)) as any[];
        const constraints = edges.filter(e => e.data && e.data.type === 'constraint');
        const references = edges.filter(e => e.data && e.data.type === 'reference');
        
        // Node Map for ShortNames
        const nodeMap = new Map<string, string>();
        elements.value.forEach((el: any) => {
            if (isNode(el)) {
                let shortName = '';
                if (el.data) {
                    if (el.data.machine_shortName) shortName = el.data.machine_shortName;
                    else if (el.data.problemdomain_shortname) shortName = el.data.problemdomain_shortname;
                    else if (el.data.system_shortName) shortName = el.data.system_shortName;
                    // For Requirements, the backend validator checks against requirement_context, NOT shortname.
                    // So we must map ID to context here for connections to work in checkProblem.
                    else if (el.data.requirement_context) shortName = el.data.requirement_context;
                    else if (el.data.requirement_shortname) shortName = el.data.requirement_shortname;
                    else if (el.data.externalentity_shortname) shortName = el.data.externalentity_shortname;
                    else if (el.data.intent_shortname) shortName = el.data.intent_shortname;
                }
                
                if (shortName) nodeMap.set(el.id, shortName);
            }
        });

          if (constraints.length > 0) {
            if (!currentProject.problemDiagram) currentProject.problemDiagram = {};
            currentProject.problemDiagram.constraintList = constraints.map((edge: any) => {
                 const reqShort = nodeMap.get(edge.source) || edge.source;
                 const domainShort = nodeMap.get(edge.target) || edge.target;
                 
                 const sourceNode = elements.value.find(el => el.id === edge.source);
                 const targetNode = elements.value.find(el => el.id === edge.target);
                 
                 let fromShort = "";
                 let toShort = "";
                 
                 const isReq = (n: any) => n && (n.type === 'requirement' || (n.data && n.data.type === 'requirement'));
                 
                 if (isReq(sourceNode) && !isReq(targetNode)) {
                     fromShort = domainShort;
                     toShort = reqShort;
                 } else if (isReq(targetNode) && !isReq(sourceNode)) {
                     fromShort = domainShort;
                     toShort = reqShort;
                 } else {
                      fromShort = edge.data.constraint_from || domainShort;
                      toShort = edge.data.constraint_to || reqShort;
                 }

                 return {
                     constraint_no: edge.data.constraint_no || 0,
                     constraint_name: edge.label || "con",
                     constraint_description: edge.label || "con",
                     constraint_from: fromShort,
                     constraint_to: toShort,
                      phenomenonList: parsePhenomenonList(edge.data.constraint_phenomenon_list, true, fromShort)
                  };
            });
        } else {
             if (currentProject.problemDiagram) currentProject.problemDiagram.constraintList = [];
        }

        if (references.length > 0) {
            if (!currentProject.problemDiagram) currentProject.problemDiagram = {};
            currentProject.problemDiagram.referenceList = references.map((edge: any) => {
                 const reqShort = nodeMap.get(edge.source) || edge.source;
                 const domainShort = nodeMap.get(edge.target) || edge.target;

                 const sourceNode = elements.value.find(el => el.id === edge.source);
                 const targetNode = elements.value.find(el => el.id === edge.target);

                 let fromShort = "";
                 let toShort = "";
                 
                 const isReq = (n: any) => n && (n.type === 'requirement' || (n.data && n.data.type === 'requirement'));
                 
                 if (isReq(sourceNode) && !isReq(targetNode)) {
                     fromShort = domainShort;
                     toShort = reqShort;
                 } else if (isReq(targetNode) && !isReq(sourceNode)) {
                     fromShort = domainShort;
                     toShort = reqShort;
                 } else {
                      fromShort = edge.data.reference_from || domainShort;
                      toShort = edge.data.reference_to || reqShort;
                 }

                 return {
                     reference_no: edge.data.reference_no || 0,
                     reference_name: edge.label || "ref",
                     reference_description: edge.label || "ref",
                     reference_from: fromShort,
                     reference_to: toShort,
                      phenomenonList: parsePhenomenonList(edge.data.reference_phenomenon_list, true, fromShort)
                  };
            });
        } else {
             if (currentProject.problemDiagram) currentProject.problemDiagram.referenceList = [];
        }

        // Also sync Context Diagram elements because Problem Diagram checks might need them
        // (e.g. references point to domains which are in Context Diagram)
        const pdNodes = elements.value.filter(n => isNode(n) && (n.type === 'problemDomain' || (n.data && n.data.type === 'problemDomain'))) as any[];
        if (pdNodes.length > 0) {
            if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
            currentProject.contextDiagram.problemDomainList = pdNodes.map((n: any) => ({
                problemdomain_no: n.data.problemdomain_no || 0,
                problemdomain_name: n.data.problemdomain_name || 'PD',
                problemdomain_shortname: n.data.problemdomain_shortname || 'PD1',
                problemdomain_type: n.data.problemdomain_type || 'Causal',
                problemdomain_property: n.data.problemdomain_property || 'GivenDomain',
                problemdomain_x: n.position.x,
                problemdomain_y: n.position.y,
                problemdomain_w: n.data.problemdomain_w || 100,
                problemdomain_h: n.data.problemdomain_h || 50
            }));
        }

        const interfaces = edges.filter(e => e.data && e.data.type === 'interface');
        if (interfaces.length > 0) {
            if (!currentProject.contextDiagram) currentProject.contextDiagram = {};
            currentProject.contextDiagram.interfaceList = interfaces.map((edge: any, index: number) => {
                   const fromShort = nodeMap.get(edge.source) || edge.source;
                   const toShort = nodeMap.get(edge.target) || edge.target;
                   
                   // Prioritize edge.data.interface_from if set (user override), else use graphical source
                   const interfaceFrom = edge.data.interface_from || fromShort;

                   return {
                       interface_no: edge.data.interface_no || (index + 1),
                       interface_name: edge.label || `int${index+1}`,
                       interface_description: edge.label || `int${index+1}`,
                       interface_from: interfaceFrom,
                       interface_to: toShort,
                       phenomenonList: parsePhenomenonList(edge.data.interface_phenomenon_list, false, interfaceFrom),
                       interface_x1: 0,
                       interface_y1: 0,
                       interface_x2: 0,
                       interface_y2: 0,
                       isintent: false
                   };
            });
        } else {
             if (currentProject.contextDiagram) currentProject.contextDiagram.interfaceList = [];
        }

        const urlCheck = `${CONFIG.backend.baseUrl}${CONFIG.backend.endpoints.checkCorrectProblem}`;
        console.log('Checking problem with project:', currentProject);
        const resCheck = await axios.post(urlCheck, currentProject, {
          headers: {
              'Content-Type': 'application/json'
          }
        });
        const errors = resCheck.data;
        
        const hasErrors = errors.some((e: any) => e.errorList && e.errorList.length > 0);
        
        if (hasErrors) {
            let message = '';
            errors.forEach((e: any) => {
                if (e.errorList && e.errorList.length > 0) {
                    message += `${e.errorList.join('\n')}\n`;
                }
            });
            
            showDialog({
                title: '检查结果',
                message: message,
                confirmButtonText: '确定'
            });
            return false;
        } else {
             showDialog({
                title: '检查结果',
                message: 'The diagram is correct.',
                confirmButtonText: '确定'
            });
            return true;
        }
    } catch (e: any) {
        console.error('Check problem failed', e);
        showDialog({
            title: 'Error',
            message: `Failed to perform check: ${e.message || e}`,
        });
        return false;
    }
  }

  function deleteElement(elementId: string) {
    // 1. Find element locally
    const index = elements.value.findIndex(el => el.id === elementId);
    if (index === -1) return;
    const element = elements.value[index];

    // 2. Remove locally
    elements.value.splice(index, 1);

    // 3. Notify Backend
    let changeEvent = null;
    
    if (isNode(element)) {
        // Node deletion
        // Map shapeType
        let shapeType = 'unknown';
        if (element.type === 'machine') shapeType = 'mac';
        else if (element.type === 'problemDomain') shapeType = 'pro';
        else if (element.type === 'requirement') shapeType = 'req';
        else if (element.type === 'system') shapeType = 'sys';
        else if (element.type === 'extentity') shapeType = 'ext';
        else if (element.type === 'intent') shapeType = 'tas';
        
        // Construct the shape object as expected by backend for deletion
        // Often backend needs just ID or Name to identify what to delete.
        // Based on 'add' logic, backend uses shapeType + changeType.
        // For 'delete', we likely need to pass the same shape structure so backend can find it.
        changeEvent = {
            shapeType: shapeType,
            changeType: 'delete',
            newShape: element.data, // Pass the data which contains name/no/etc
            projectPath: projectAddress.value
        };
    } else if (isEdge(element)) {
        // Edge deletion
        let shapeType = 'unknown';
        if (element.data && element.data.type === 'interface') shapeType = 'int';
        else if (element.data && element.data.type === 'constraint') shapeType = 'con';
        else if (element.data && element.data.type === 'reference') shapeType = 'ref';
        
        changeEvent = {
            shapeType: shapeType,
            changeType: 'delete',
            newShape: element.data,
            projectPath: projectAddress.value
        };
    }

    if (changeEvent) {
        const message = {
            jsonrpc: '2.0',
            id: Date.now(),
            method: 'Diagram/didChange',
            params: {
                diagram: { uri: projectAddress.value, version: projectVersion.value },
                contentChanges: [changeEvent]
            }
        };

        if (ws.value && ws.value.readyState === WebSocket.OPEN) {
            ws.value.send(JSON.stringify(message));
        }
    }
  }

  return {
    isConnected,
    elements,
    connect,
    addNodeToProject,
    addInterface,
    addConstraint,
    addReference,
    updateShape,
    deleteElement, // Export delete function
    createNewProject,
    getProjectList,
    openProject,
    saveProject,
    downloadProject,
    initStep2,
    checkContext,
    checkProblem
  };
});
