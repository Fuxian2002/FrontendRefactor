# PF2-web2（2022 年 08 月 27 日，更换服务器）

## 简介
Problem projection is an effective technique for decomposing problems. That is, each sub-problem is considered as a projection of the main problem concerned only with the phenomena relevant to that sub-problem. This tool uses the concept of projection from relational algebra and combine it with concepts from the PF and scenario-based approaches to present a conceptual model for conducting problem projection in requirements engineering.

## 项目地址
服务器"/home/frontend/PF2-web2"：前端

服务器"/home/backend/PF2-web2/web2-backend-0.0.1-SNAPSHOT.jar"：后端程序打包的 jar 文件

服务器"/home/backend_resources/PF"：后端程序所使用的资源文件

用户访问网址："http://re4cps.org/PF2-web2/"

注意：PF2-web1、PF2-web2 、PF2-web4 共同使用一份资源文件"/home/backend_resources/PF"

## 部署方式

### 前端

使用 80 端口，用 nginx 部署，具体步骤：

1）在本地使用命令"ng build --prod"编译打包前端项目源代码，在项目根目录下的 dist 中出现 PF2-web2 目录

2）将该 PF2-web2 目录上传至服务器"/home/frontend/"目录下，即"/home/frontend/PF2-web2"

3）使用命令"cd /usr/local/nginx/conf"切换到conf路径下

4）使用命令"vim nginx.conf"，对 nginx.conf 文件进行编辑，编辑后保存退出。按照如下内容编辑：


```
    server {
        listen   80;  # 端口号
        server_name  mainPage;  # 名字，自己随便取的

        location /PF2-web2 {
            alias    /home/frontend/PF2-web2;  # 前端项目根目录
            index    index.html;  # 首页文件
            try_files $uri $uri/ /index.html; #try_files先寻找名为 $uri 文件，没有则寻找 $uri/ 文件，再没有就寻找/index.html。这是为了防止出现页面刷新 404 情况发生 
        }
    }

```

5）使用命令"cd /usr/local/nginx/sbin"切换到sbin路径下

6）使用命令"./nginx -s reload"重启服务

### 后端
使用 8087 端口，具体步骤：

1）在本地的后端项目根目录下执行命令"mvn clean install"，在 target 目录下会生成 web2-backend-0.0.1-SNAPSHOT.jar 文件

2）将 web2-backend-0.0.1-SNAPSHOT.jar 文件上传至服务器"/home/backend/PF2-web2/"目录下

3）在服务器上执行命令"cd /home/backend/PF2-web2/"切换到 PF2-web2 路径下

4）在服务器上执行命令"nohup java -jar web2-backend-0.0.1-SNAPSHOT.jar --server.port=8087 &"，接着输入"exit"并回车

## 重启服务方法

### 前端

在服务器上相关文件完整的情况下，执行部署方式-前端中的第 5、6 步

### 后端

在服务器上相关文件完整的情况下，执行部署方式-后端中的第 3、4 步

## 代码修改记录

2022 年 08 月 27 日，新建仓库，并将现有代码上传至此仓库

## 踩坑记录

1）将代码移植到其他平台时，记得修改相关路径


# PF2-web2（原说明，2022 年 08 月 27 日之前的）

#### 介绍



#### 软件架构



#### 使用教程



> 1.  后端<br>
> 找到com.example.demo包中的Web4BackendApplication.java文件，右键点击Run As，选择Spring Boot App即可运行，启动成功后如下图所示：
> ![输入图片说明](https://images.gitee.com/uploads/images/2020/0925/204006_f36372f4_2119277.png "屏幕截图.png")
> 2.  前端<br>
> 进入前端文件目录后，输入`ng serve --open`即可启动

#### 部署教程



> 1.  后端<br>
>     1.  nohup java -jar PF2-new-0.0.1-SNAPSHOT.jar --server.port=8089 &

#### 使用说明



#### 代码说明



>  **2020.9.25** <br>
> 实现了新建项目的一系列过程。
> 
> TODO list：<br>
> 1.画图时不能放大缩小，报错：ERROR TypeError: Cannot read property 'scale' of undefined    **done** <br>
> 2.delete   delete Int时会跳到情景图    **done** <br>
> 3.情景图   部分Int文本显示不出来    **done** <br>
> 4.修改Int时报错：DrawingboardComponent.html:196 ERROR TypeError: this.project.getDescription is not a function    **done** <br>


>  **2020.9.27** <br>
> 完成了保存项目和打开项目的一系列过程。
> 
> TODO list：<br>
> 1.打开项目之后右边的流程跳转到第一步，而中间显示绘制情景图的页面。    **done** <br>



>  **2020.9.29** <br>
> 完成了添加子问题图的一系列过程（新建-保存-打开）。

>  **2020.10.8** <br>
> 第一次部署保存备份（实现了将两个工具的功能合在一起）。

