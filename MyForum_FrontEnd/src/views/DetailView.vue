<template>
    <div @scroll="infiniteScroll" infinite-scroll-distance="10"  v-infinite-scroll="loadComment" :infinite-scroll-immediate="infinite_scroll.immediate" :infinite-scroll-disabled="infinite_scroll.disabled" class="infinite-list" >
    <div>
        <div class="toolbar">
            <div style="margin-right: 60px">
                <div @click="userView(user.id)">
                    <el-image :src="user.avatar" style="height: 40px;width: 40px; border-radius: 20px">
                    </el-image>
                </div>
                <div style="text-align: center" @click="userView(user.id)">{{user.nickname}}</div>
            </div>
        </div>
        <div style="flex: 1; width: 80%; margin: auto;margin-top: 20px">

            <el-row class="postingCSS" >
                <el-col :span="2">
                    <el-row>
                        <el-image style="margin:auto auto;width: 40px; height: 40px; float: left;" :src="postingVisit.auth.avatar" @click="userView(posting.auth.id)"/>
                    </el-row>
                    <el-row>
                        <el-text class="mx-1" style="margin:auto auto;font-size: 12px; color: white;overflow: auto;" @click="userView(postingVisit.auth.id)">{{postingVisit.auth.nickname}}</el-text>
                    </el-row>
                </el-col>
                <el-col :span="20">
                <el-col >
                    <el-row>
                        <div v-if="postingVisit.tags != null" style="height:24px;">
                            <el-tag type="info" v-for="tag in postingVisit.tags" :key="tag.id">  {{tag.content}}</el-tag>
                        </div>
                    </el-row>
                    <el-row>
                        <el-text  type="success"  style="font-size: 24px;overflow: auto;" >&nbsp;&nbsp;{{postingVisit.posting.title}}</el-text>
                    </el-row>
                    <el-row>
                        <el-text  style="font-size: 18px; color: white;overflow: auto;"  >&nbsp;&nbsp;{{postingVisit.posting.content}}</el-text>
                    </el-row>
                    <el-row v-for="(item, index) in postingVisit.files" style="display: inline;margin-top: 20px; margin-left: 20px;" :key="index">
                        <span>
                        <el-image v-if="item.type === 'image/jpeg'" :src="item.url" style="max-width: 100px; max-height: 100px;">
                        </el-image>
                        <video v-if="item.type === 'video/mp4'" style="width: 100%;height: 100%; max-height: 400px; max-width: 400px"  alt="Preview Video" controls>
                            <source :src="item.url"  type="video/mp4">
                        </video>
                        </span>
                    </el-row>
                    <el-row style="height: auto; width: 100%">
                        <el-icon color="white"><View /></el-icon>
                        <el-text class="mx-1"   style="font-size: 12px;color: white" >{{postingVisit.posting.record}}</el-text>
                        <el-icon color="white" style="margin-left: 5px"><Opportunity /></el-icon>
                        <el-text class="mx-1"   style="font-size: 12px;color: white;" >{{postingVisit.likeSum}}</el-text>
                        <el-icon color="white" style="margin-left: 5px"><ChatDotRound /></el-icon>
                        <el-text class="mx-1"   style="font-size: 12px;color: white" >{{postingVisit.forwardSum}}</el-text>
                        <el-icon color="white" style="margin-left: 5px"><Share /></el-icon>
                        <el-text class="mx-1"   style="font-size: 12px;color: white" >{{postingVisit.forwardSum}}</el-text>
                    </el-row>
                </el-col>
                </el-col>
            </el-row>
            <el-row style="margin-top: 20px">
                <el-row v-for="item in comment" :key="item.comment.id" style=" width: 100%; height: auto; background-color: #2c3e50; border-radius: 10px;margin-top: 20px">
                    <el-col :span="2">
                        <el-row>
                                <el-image :src="item.auth.avatar" style="width: 40px;height: 40px;margin:auto auto; float: left; border-radius: 180px"></el-image>
                        </el-row>
                        <el-row>
                            <el-text class="mx-1" style="margin:auto auto;font-size: 12px; color: white;overflow: auto;" @click="userView(item.auth.id)">{{item.auth.nickname}}</el-text>
                        </el-row>
                    </el-col >
                    <el-col :span="20" style="color: white;">
                        <el-row style="padding-top: 20px; overflow: auto">
                            {{item.comment.content}}
                        </el-row>
                        <el-row style="margin-top: 20px; margin-left: 20px;display: inline" v-for="(item, index) in item.files" :key="index">
                        <span>
                        <el-image v-if="item.type === 'image/jpeg'" :src="item.url" style="max-width: 100px; max-height: 100px;">
                        </el-image>
                        <video v-if="item.type === 'video/mp4'" style="width: 100%;height: 100%; max-width: 400px; max-height: 400px;"  alt="Preview Video" controls>
                            <source :src="item.url"  type="video/mp4">
                        </video>
                        </span>
                        </el-row>
                    </el-col>
                </el-row>
            </el-row>
        </div>
    </div>
    </div>
    <el-dialog
        v-model="commentAdd.visible"
        title="写评论"
        width="80%"
        align-center
    >
        <el-input
            v-model="commentAdd.content"
            :autosize="{ minRows: 4, maxRows: 8 }"
            type="textarea"
            placeholder="内容"
        />
        <el-upload action="#"
                   ref="upload"
                   list-type="picture-card"
                   :file-list="commentAdd.files"
                   :multiple="true"
                   :auto-upload="false"
                   name="multipartFiles"
                   :on-change="(file,fileList)=>commentUploadChange(file,fileList)"
                   :on-success="commentUploadChangeSuccess"
        >
            <el-icon><Plus /></el-icon>
            <template #file="{file}">
                <div>
                    <img class="el-upload-list__item-thumbnail" :src="file.url" alt="" v-if="file.raw.type==='image/jpeg'|| file.raw.type==='image/png'" />
                    <video class="el-upload-list__item-thumbnail" :src="file.url" alt="" v-if="file.raw.type==='video/mp4'" />
                    <span class="el-upload-list__item-actions">
                  <span
                      class="el-upload-list__item-preview"
                      @click="handleFilePreview(file)"
                  >
                    <el-icon><zoom-in /></el-icon>
                  </span>
                  <span
                      v-if="!commentAdd.visible"
                      class="el-upload-list__item-delete"
                      @click="handleCommentFileRemove(file)"
                  >
                    <el-icon><Delete /></el-icon>
                  </span>
                </span>
                </div>
            </template>
        </el-upload>
        <el-dialog v-model="filePreview.visible" >
            <img :src="filePreview.url" style="width: 100%;height: 100%"  alt="Preview Image" v-if="filePreview.type==='image/jpeg' || filePreview.type==='image/png' "/>
            <video style="width: 100%;height: 100%" v-if="filePreview.type==='video/mp4'" alt="Preview Video" controls>
                <source :src="filePreview.url"  type="video/mp4">
            </video>
        </el-dialog>
        <template #footer>
      <span class="dialog-footer">
        <el-button @click="commentAdd.visible = false">取消</el-button>
        <el-button type="primary" @click="addComment()">确认</el-button>
      </span>
        </template>
    </el-dialog>
    <el-button type="primary"  @click="commentAdd.visible = true" style="right: 40px; top:120px; position: fixed;"><el-icon><Edit /></el-icon></el-button>
    <el-affix  v-show="topBack" @click="topBackScroll">
        <el-button style="right: 40px; top:160px; position: fixed;" type="primary"><el-icon><Top /></el-icon></el-button>
    </el-affix>
</template>

<script>
import common from "@/components/common.vue";

export default {
    name: "DetailView",
    data(){
        return{
            topBack:false,
            pageCount: 1,
            infinite_scroll:{
                disabled:false,
                immediate:false,
            },
            filePreview:{
                visible : false,
                url:"",
                type:"",
            },
            user:{
                avatar: "",
                nickname:"",
                id:-1,
            },postingVisit:{
                posting: {
                    id: 17,
                    title: "To clear or reload various internal caches, flush tables, or acquire locks, control-click           ",
                    content: "Typically, it is employed as an encrypted version of Telnet. If your Internet Service Provider (ISP) does not provide direct access to its server, Secure Tunneling Protocol (SSH) / HTTP is another solution. To successfully establish a new connection to local/remote server - no matter via SSL, SSH or HTTP, set the database login information in the General tab. To successfully establish a new connection to local/remote server - no matter via SSL, SSH or HTTP, set the database login information in the General tab. Navicat allows you to transfer data from one database and/or schema to another with detailed analytical process. Secure Sockets Layer(SSL) is a protocol for transmitting private documents via the Internet. SQL Editor allows you to create and edit SQL text, prepare and execute selected queries. If you wait, all that happens is you get older. Flexible settings enable you to set up a custom key for comparison and synchronization. Navicat Cloud could not connect and access your databases. By which it means, it could only store your connection settings, queries, model files, and virtual group; your database passwords and data (e.g. tables, views, etc) will not be stored to Navicat Cloud. Anyone who has never made a mistake has never tried anything new. A comfort zone is a beautiful place, but nothing ever grows there. All the Navicat Cloud objects are located under different projects. You can share the project to other Navicat Cloud accounts for collaboration. Champions keep playing until they get it right. The past has no power over the present moment. The Navigation pane employs tree structure which allows you to take action upon the database and their objects through their pop-up menus quickly and easily. It collects process metrics such as CPU load, RAM usage, and a variety of other resources over SSH/SNMP. In the middle of winter I at last discovered that there was in me an invincible summer. If your Internet Service Provider (ISP) does not provide direct access to its server, Secure Tunneling Protocol (SSH) / HTTP is another solution. Genius is an infinite capacity for taking pains. Navicat is a multi-connections Database Administration tool allowing you to connect to MySQL, Oracle, PostgreSQL, SQLite, SQL Server, MariaDB and/or MongoDB databases, making database administration to multiple kinds of database so easy. Difficult circumstances serve as a textbook of life for people. You can select any connections, objects or projects, and then select the corresponding buttons on the Information Pane. Export Wizard allows you to export data from tables, collections, views, or query results to any available formats. Always keep your eyes open. Keep watching. Because whatever you see can inspire you. There is no way to happiness. Happiness is the way.",
                    record: 628,
                    auth: 2,
                    create_time: "2005-07-11T11:37:52"
                },
                auth: {
                    id: 2,
                    username: "123456",
                    password: "33144b298424e4c09a67ae66b546a538",
                    nickname: "test",
                    salt: "ustc",
                    role: "normal",
                    avatar: "http:///127.0.0.1:9000/weibo-avatar/40233721-c3c6-4c4f-b339-de7598129765.jpg",
                    introduce: "还没有自我介绍",
                    create_time: "2024-02-12T16:33:44"
                },
                commentSum: 1,
                likeSum: 2,
                forwardSum: 0,
                tags: []
            },comment:[
                {
                    comment: {
                        id: 6,
                        content: "Success consists of going from failure to failure without loss of enthusiasm. The repository database can be an existing MySQL, MariaDB, PostgreSQL, SQL Server, or Amazon RDS instance. There is no way to happiness. Happiness is the way. Navicat Data Modeler is a powerful and cost-effective database design tool which helps you build high-quality conceptual, logical and physical data models. If the Show objects under schema in navigation pane option is checked at the Preferences window, all database objects are also displayed in the pane. Navicat Monitor can be installed on any local computer or virtual machine and does not require any software installation on the servers being monitored. Typically, it is employed as an encrypted version of Telnet. How we spend our days is, of course, how we spend our lives. You must be the change you wish to see in the world. To open a query using an external editor, control-click it and select Open with External Editor. You can set the file path of an external editor in Preferences. To successfully establish a new connection to local/remote server - no matter via SSL or SSH, set the database login information in the General tab. Sometimes you win, sometimes you learn. Navicat 15 has added support for the system-wide dark mode. Typically, it is employed as an encrypted version of Telnet. I will greet this day with love in my heart. If your Internet Service Provider (ISP) does not provide direct access to its server, Secure Tunneling Protocol (SSH) / HTTP is another solution. Navicat allows you to transfer data from one database and/or schema to another with detailed analytical process. Anyone who has ever made anything of importance was disciplined. You cannot save people, you can just love them. Navicat allows you to transfer data from one database and/or schema to another with detailed analytical process. I will greet this day with love in my heart. The past has no power over the present moment. How we spend our days is, of course, how we spend our lives. Navicat 15 has added support for the system-wide dark mode. Navicat 15 has added support for the system-wide dark mode. To clear or reload various internal caches, flush tables, or acquire locks, control-click your connection in the Navigation pane and select Flush and choose the flush option. You must have the reload privilege to use this feature. A comfort zone is a beautiful place, but nothing ever grows there. In the Objects tab, you can use the List List, Detail Detail and ER Diagram ER Diagram buttons to change the object view. HTTP Tunneling is a method for connecting to a server that uses the same protocol (http://) and the same port (port 80) as a web server does. To successfully establish a new connection to local/remote server - no matter via SSL, SSH or HTTP, set the database login information in the General tab. SQL Editor allows you to create and edit SQL text, prepare and execute selected queries. Navicat Data Modeler is a powerful and cost-effective database design tool which helps you build high-quality conceptual, logical and physical data models. Navicat Data Modeler enables you to build high-quality conceptual, logical and physical data models for a wide variety of audiences. The reason why a great man is great is that he resolves to be a great man. Instead of wondering when your next vacation is, maybe you should set up a life you don’t need to escape from. If you wait, all that happens is you get older. Typically, it is employed as an encrypted version of Telnet. To successfully establish a new connection to local/remote server - no matter via SSL, SSH or HTTP, set the database login information in the General tab.",
                        auth: 5,
                        postingId: null,
                        create_time: "2007-09-12T20:26:03"
                    },
                    auth: {
                        id: 5,
                        avatar: "http://192.168.8.101:9000/weibo-avatar/default.jpg",
                        nickname: "秦嘉伦"
                    },
                    files: [],
                    likeSum: 0,
                    isLike: false
                }
            ],
            commentAdd:{
                visible:false,
                types:[],
                files:[],
                content:''
            }
        }
    },mounted() {
        if(common.verifyLogin()){
            this.user.avatar = localStorage.getItem("weibo_avatar")
            this.user.nickname = localStorage.getItem("weibo_nickname")
            this.user.id = localStorage.getItem("weibo_userId")
        }
        common.TokenAxios({
            method:"GET",
            url:common.BackEndIP + "posting/id",
            params:{
                postingId: localStorage.getItem("weibo_targetPostingId")
            }
        }).then((resp)=>{
            console.log(resp)
            if(resp.data.code === "200"){
                this.postingVisit = resp.data.data[0]
            }
        })
        common.TokenAxios({
            method:"GET",
            url:common.BackEndIP + "comment/all/"+this.postingVisit.posting.id,
            params:{
                userId: this.user.id,
                page: 1,
            }
        }).then((resp)=>{
            console.log(resp)
            if(resp.data.code === "200"){
                this.comment = resp.data.data
            }
        })
    },methods:{
        infiniteScroll(){
            const top = document.querySelector(".infinite-list").scrollTop
            if(top > 600) this.topBack = true
            else this.topBack = false
        },
        topBackScroll(){
            this.topBack = false;
            document.querySelector(".infinite-list").scrollTop = 0;
        },loadComment(){
            common.TokenAxios({
            method:"GET",
            url:common.BackEndIP + "comment/all/"+this.postingVisit.posting.id,
            params:{
                userId: this.user.id,
                page: this.pageCount+1,
            }
        }).then((resp)=>{
            console.log(resp)
            if(resp.data.code === "200"){
                this.comment.push.apply(this.comment,resp.data.data)
                this.pageCount++
            }else {
                if(this.infinite_scroll.disabled === false)
                    alert(resp.data.msg)
                this.infinite_scroll.disabled = true;
            }
        })

        },userView(targetUserId){
            if(common.verifyLogin()){
                localStorage.setItem("weibo_targetUserId", targetUserId)
                window.location.reload()
            }
            else {
                this.$router.push(common.base + '/login')
            }
        },commentUploadChange(file,fileList){
            this.commentAdd.files=fileList
        },commentUploadChangeSuccess(){
            location.reload()
        },notify(title, msg,type) {
            this.$notify({
                title: title,
                message: msg,
                position: 'top-left',
                type: type,
            });
        },handleFilePreview(file){
            this.filePreview.url = file.url
            this.filePreview.type = file.raw.type
            this.filePreview.visible = true
        },handleCommentFileRemove(file){
            this.$refs.upload.handleRemove(file);
        },addComment(){
            let formData = new FormData(); //  用FormData存放上传文件
            // eslint-disable-next-line no-unused-vars
            this.commentAdd.files.forEach((val,index) => {
                formData.append("files",val.raw)
                formData.append("types",val.raw.type)
            });
            formData.set("content", this.commentAdd.content)
            formData.set("postingId", this.postingVisit.posting.id)
            formData.set("userId", this.user.id)
            common.TokenAxios({
                method:"post",
                url:common.BackEndIP + 'comment/add',
                data:formData,
                headers:{
                    "Content-Type": "multipart/form-data"
                }
            }).then((resp)=>{
                if(resp.data.code === "200"){
                    console.log(resp)
                }else {
                    alert(resp.data.msg)
                }
            })
            this.commentAdd.visible = false
        }
    }
}
</script>

<style scoped>
.postingCSS{
    height: auto;
    width: auto;
    background-color: #2c3e50;
    box-shadow: rgba(6, 24, 44, 0.4) 0px 0px 0px 2px, rgba(6, 24, 44, 0.65) 0px 4px 6px -1px, rgba(255, 255, 255, 0.08) 0px 1px 0px inset;
}
.infinite-list {
    margin-top: 100px;
    overflow: auto;
    right: 0;
    height: calc(100% - 100px);
    list-style: none;
}
.toolbar {
    position: fixed;
    display: inline-flex;
    align-items: center;
    justify-content: right;
    text-align: right;
    font-size: 16px;
    top: 0;
    width: 100%;
    height: 15%;
    z-index:10;
    background-color: #a9c09a;
}
</style>