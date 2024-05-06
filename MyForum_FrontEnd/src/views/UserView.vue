<template>
    <div @scroll="infiniteScroll" infinite-scroll-distance="10"  v-infinite-scroll="loadPosting" :infinite-scroll-immediate="infinite_scroll.immediate" :infinite-scroll-disabled="infinite_scroll.disabled" class="infinite-list" >
    <div class="toolbar">
        <div style="margin-right: 60px">
            <div @click="userView(user.id)">
                <el-image :src="user.avatar" style="height: 40px;width: 40px; border-radius: 20px">
                </el-image>
            </div>
            <div style="text-align: center" @click="userView(user.id)">{{user.nickname}}</div>
        </div>
    </div>
    <div style=" flex: 1;left: 0; width: 75%;height: 85%">
        <el-row v-for="item in postingOfUser"
                        :key="item.posting.id"
                        class="postingCSS" >
                    <el-col >
                        <el-row>
                            <div v-if="item.tags != null" style="height:24px;">
                                <el-tag type="info" v-for="tag in item.tags" :key="tag.id">  {{tag.content}}</el-tag>
                            </div>
                        </el-row>
                        <el-row>
                            <el-text  type="success"  style="font-size: 24px;overflow: auto;" @click="detailView(item.posting.id)">&nbsp;&nbsp;{{item.posting.title}}</el-text>
                        </el-row>
                        <el-row>
                            <el-text  style="font-size: 18px; color: white;overflow: auto;" @click="detailView(item.posting.id)" >&nbsp;&nbsp;{{item.posting.content}}</el-text>
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
                        <el-row style="height: auto">
                            <el-icon color="white"><View /></el-icon>
                            <el-text class="mx-1"   style="font-size: 12px;color: white" @click="detailView(item.posting.id)">{{item.posting.record}}</el-text>
                            <el-icon color="white" style="margin-left: 5px"><Opportunity /></el-icon>
                            <el-text class="mx-1"   style="font-size: 12px;color: white;" @click="detailView(item.posting.id)">{{item.likeSum}}</el-text>
                            <el-icon color="white" style="margin-left: 5px"><ChatDotRound /></el-icon>
                            <el-text class="mx-1"   style="font-size: 12px;color: white" @click="detailView(item.posting.id)">{{item.forwardSum}}</el-text>
                            <el-icon color="white" style="margin-left: 5px"><Share /></el-icon>
                            <el-text class="mx-1"   style="font-size: 12px;color: white" @click="detailView(item.posting.id)">{{item.forwardSum}}</el-text>
                        </el-row>
                    </el-col>
                </el-row>
    </div>
    <div style="position:fixed;right: 20px;top: 0px; width: 25%; height: 100%;">
        <el-row style="margin-top:120px; width: 100%; height: auto;background-color: #2c3e50; border-radius: 10px;">
            <el-col>
            <el-row>
                <div style="margin: auto auto;">
                    <el-upload
                        :disabled="user.id!=userVisit.targetUser.id"
                        :action="userAlter.avatarAlter.url"
                        ref="upload"
                        :before-upload="rawFile => beforeAvatarUpload(rawFile)"
                        :on-success="afterAvatarUpload"
                        :show-file-list="false"
                        :data="userAlter.avatarAlter.data"
                        :headers="userAlter.avatarAlter.headers"
                    >
                    <el-image :src="userVisit.targetUser.avatar" style="width: 80px;height: 80px; border-radius: 180px"></el-image>
                    </el-upload>
                </div>
            </el-row>
            <el-row>
                <div @click="dialogVisible.infoList=true"  style="margin: 0 auto;color: white;font-size: 24px;overflow: auto;">
                    {{userVisit.targetUser.nickname}}
                </div>
            </el-row>
            <el-row style="height: 40px">
                <div style="margin: 0 auto;" v-if="user.id != userVisit.targetUser.id">
                    <el-button v-if="userVisit.isLike===false" type="primary" @click="likeUserVisit()">关注</el-button>
                    <el-button v-if="userVisit.isLike===true" type="danger" @click="likeUserVisit()">已关注</el-button>
                </div>
                <div style="margin: 0 auto;" v-if="user.id != userVisit.targetUser.id">
                    <el-button v-if="userVisit.isUnlike===false" type="info"  @click="unlikeUserVisit()">拉黑</el-button>
                    <el-button v-if="userVisit.isUnlike===true" type="default"  @click="unlikeUserVisit()">已拉黑</el-button>
                </div>
            </el-row>
            <el-row>
                <el-col :span="8" style="background-color: #a9c09a;border-radius: 10px;">
                    <el-row><div style="margin: auto auto;color: white;">关注</div></el-row>
                    <el-row><div style="margin: auto auto;color: white;" ><el-button text  @click="dialogVisible.likeList = true">{{userVisit.likeCount}}</el-button></div></el-row>
                </el-col>
                <el-col :span="8" style="background-color: #a9c09a;border-radius: 10px;">
                    <el-row ><div style="margin: auto auto;color: white;">粉丝</div></el-row>
                    <el-row><div style="margin: auto auto;color: white;"><el-button text  @click="dialogVisible.fanList = true">{{userVisit.fanCount}}</el-button></div></el-row>
                </el-col>
                <el-col :span="8" style="background-color: #a9c09a;border-radius: 10px">
                    <el-row ><div style="margin: auto auto;color: white;">黑名单</div></el-row>
                    <el-row ><div style="margin: auto auto;color: white;"><el-button text  @click="dialogVisible.unlikeList = true">{{userVisit.unlikeCount}}</el-button></div></el-row>
                </el-col>
            </el-row>

            </el-col>
        </el-row>
        <el-row style="width: 100%; height: 30%;margin-top:10px;background-color: #2c3e50;border-radius: 10px;">
            <el-col>
                    <el-row><div style="margin: 20px auto;color: white;">个人介绍</div></el-row>
                    <el-row>
                        <div style="margin: auto auto;color: white;font-size: 12px" @click="dialogVisible.infoList=true"  >
                            {{userVisit.targetUser.introduce}}
                        </div>
                    </el-row>
            </el-col>
        </el-row>
        <el-row v-if="user.id==userVisit.targetUser.id" style="text-align: center;margin-top:30px;">
            <div style="width: 100%;height: 100%;">
                <el-button type="primary" @click="chatView" size="large">联系</el-button>
            </div>
        </el-row>
    </div>
    </div>
    <el-dialog
        title="账户修改"
        v-model="dialogVisible.alterList"
        width="50%"
        center>
        <el-form :model="userAlter.accountAlter" :rules="rules" ref="userForm">
            <el-form-item prop="username">
                <el-input size="default"  v-model="userAlter.accountAlter.username" placeholder="请输入账号"></el-input>
            </el-form-item>
            <el-form-item prop="passwordOld">
                <el-input size="default" v-model="userAlter.accountAlter.passwordOld" placeholder="请输入旧密码"></el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input size="default" v-model="userAlter.accountAlter.password" show-password placeholder="请输入新密码"></el-input>
            </el-form-item>
            <el-form-item prop="confirmPassword">
                <el-input v-model="userAlter.accountAlter.confirmPassword" show-password placeholder="请确认新密码"></el-input>
            </el-form-item>
        </el-form>
        <slot name="footer" class="dialog-footer">
            <el-button @click="dialogVisible.alterList = false">取 消</el-button>
            <el-button type="primary" @click="alterUser">确 定</el-button>
        </slot>
    </el-dialog>
    <el-dialog
        title="信息修改"
        v-if="user.id == userVisit.targetUser.id"
        v-model="dialogVisible.infoList"
        width="50%"
        center>
        <el-form :model="userAlter.infoAlter" :rules="rules" ref="userInfoForm">
            <el-form-item prop="nickname">
                <el-input size="default"  v-model="userAlter.infoAlter.nickname" placeholder="可修改昵称"></el-input>
            </el-form-item>
            <el-form-item prop="introduce">
                <el-input size="default" v-model="userAlter.infoAlter.introduce" placeholder="可修改介绍"></el-input>
            </el-form-item>
        </el-form>
        <slot name="footer" class="dialog-footer">
            <el-button @click="dialogVisible.infoList = false">取 消</el-button>
            <el-button type="primary" @click="alterUserInfo">确 定</el-button>
        </slot>
    </el-dialog>
    <el-dialog v-model="filePreview.dialog.visible" >
        <img :src="filePreview.dialog.url" style="width: 100%;height: 100%"  alt="Preview Image"/>
    </el-dialog>
    <el-dialog v-model="dialogVisible.likeList" title="关注列表" style="width: 60%">
        <el-table :data="userVisit.likeList">
            <el-table-column  label="头像" width="150" >
                <template #default="scope">
                    <div style="display: flex; align-items: center">
                        <el-image :src="scope.row.avatar" style="width: 40px;height: 40px; border-radius: 180px" @click="userView(scope.row.id)">{{scope.row.nickname}}</el-image>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" width="200" />
        </el-table>
    </el-dialog>
    <el-dialog v-model="dialogVisible.fanList" title="粉丝列表">
        <el-table :data="userVisit.fanList">
            <el-table-column  label="头像" width="150" >
                <template #default="scope">
                    <div style="display: flex; align-items: center">
                        <el-image :src="scope.row.avatar" style="width: 40px;height: 40px; border-radius: 180px" @click="userView(scope.row.id)"></el-image>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" width="200" />
        </el-table>
    </el-dialog>
    <el-dialog v-model="dialogVisible.unlikeList" title="黑名单">
        <el-table :data="userVisit.unlikeList">
            <el-table-column  label="头像" width="150" >
                <template #default="scope">
                    <div style="display: flex; align-items: center">
                        <el-image :src="scope.row.avatar" style="width: 40px;height: 40px; border-radius: 180px" @click="userView(scope.row.id)"></el-image>
                    </div>
                </template>
            </el-table-column>
            <el-table-column prop="nickname" label="昵称" width="200" />
        </el-table>
    </el-dialog>
    <el-button v-if="user.id==userVisit.targetUser.id" type="primary"  @click="dialogVisible.alterList = true" style="right: calc(25% + 30px); top:120px; position: fixed;"><el-icon><Edit /></el-icon></el-button>
    <el-affix  v-show="topBack" @click="topBackScroll">
        <el-button style="right: calc(25% + 30px); top:160px; position: fixed;" type="primary"><el-icon><Top /></el-icon></el-button>
    </el-affix>

</template>

<script>
import common from "@/components/common.vue";

export default {
    name: "UserView",
    data(){
        const validPassword = (rule, value, callback) => {
            if (value === '') {
                callback(new Error('请再次输入密码'));
            } else if (value !== this.userAlter.accountAlter.password) {
                callback(new Error('两次输入密码不一致!'));
            } else {
                callback();
            }
        };
        return{
            singChatNickname:"",
            multiChatInfo:{
                roomNum:"",
                roomPassword:"",
            },
            topBack:false,
            rules:{
                username:[
                    {required:true, message:'请输入账号', trigger: 'blur'},
                    {min:3, max:10,message: '长度在3到10个字符', trigger: 'blur'}
                ],password:[
                    {required:true, message:'请输入新密码', trigger: 'blur'},
                    {min:6, max:18,message: '长度在6到18个字符', trigger: 'blur'}
                ],confirmPassword:[
                    {required:true, message:'请确认新密码', trigger: 'blur'},
                    {min:6, max:18,message: '长度在6到18个字符', trigger: 'blur'},
                    {validator:validPassword, trigger: "blur"},
                ],passwordOld:[
                    {required:true, message:'请输入旧密码', trigger: 'blur'},
                    {min:3, max:10,message: '长度在6到18个字符', trigger: 'blur'}
                ]
            },
            infinite_scroll:{
                disabled:false,
                immediate:false,
            },
            postingLoadCount:1,
            dialogVisible:{
              likeList:false,
              unlikeList:false,
              fanList: false,
              alterList:false,
              infoList:false,
                singleChat:false,
                multiChat:false,
            },
            user:{
                avatar:"http://127.0.0.1:9000/weibo-avatar/default.jpg",
                nickname: "游客",
                id:-1
            },
            userVisit:{
                targetUser: {
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
                isLike: false,
                isUnlike: false,
                likeCount: 1,
                likeList: [
                    {
                        id: 6,
                        avatar: "http://192.168.8.101:9000/weibo-avatar/default.jpg",
                        nickname: "姜云熙"
                    }
                ],
                unlikeCount: 1,
                unlikeList: [
                    {
                        id: 4,
                        avatar: "http://192.168.8.101:9000/weibo-avatar/default.jpg",
                        nickname: "顾岚"
                    }
                ],
                fanCount: 1,
                fanList: [
                    {
                        id: 10,
                        avatar: "http://192.168.8.101:9000/weibo-avatar/default.jpg",
                        nickname: "李睿"
                    }
                ]
            },
            userAlter:{
                infoAlter:{
                    introduce:"",
                    nickname:""
                },
                avatarAlter:{
                    url: common.UserUploadAvatar + localStorage.getItem("weibo_userId"),
                    headers:{token:localStorage.getItem("weibo_token")},
                    data:{
                        type:"",
                    }
                },
                accountAlter:{
                    username:"",
                    passwordOld:""
                }
            },
            postingOfUser:[
                {
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
                }
            ],filePreview:{
                dialog:{
                    visible : false,
                    url:"",
                    type:"",
                },
            }
        }
    },mounted() {
        if(common.verifyLogin()){
            this.user.avatar = localStorage.getItem("weibo_avatar")
            this.user.nickname = localStorage.getItem("weibo_nickname")
            this.user.id = localStorage.getItem("weibo_userId")
        }
        common.TokenAxios({
            method:"get",
            url: common.BackEndIP + "user/visit",
            params:{
                sourceUserId:this.user.id,
                targetUserId:localStorage.getItem("weibo_targetUserId"),
            },
        }).then((resp)=>{
            if(resp.status === 200){
                this.userVisit = resp.data.data;
                if(this.userVisit.targetUser.id == this.user.id){
                    this.user = this.userVisit.targetUser
                    localStorage.setItem("weibo_userId", this.user.id)
                    localStorage.setItem("weibo_nickname", this.user.nickname)
                    localStorage.setItem("weibo_avatar", this.user.avatar)
                    localStorage.setItem("weibo_introduce", this.user.introduce)
                }
            }else{
                alert(resp.data.msg)
            }
        })
        common.TokenAxios({
            method:"get",
            url: common.BackEndIP + "posting/user",
            params:{
                userId: localStorage.getItem("weibo_targetUserId"),
                page: this.postingLoadCount,
            }
        }).then((resp)=>{
            if(resp.data.code === "200"){
                this.postingOfUser = resp.data.data;
            }else{
                alert(resp.data.msg)
            }
        })
    },
    methods:{
        loadPosting(){
            common.TokenAxios({
                method: "get",
                url: common.BackEndIP + "posting/user",
                params:{
                    userId: this.user.id,
                    page: this.postingLoadCount+1,
                }
            }).then((resp) => {
                console.log(resp)
                if (resp.data.code === "200") {
                    this.postingOfUser.push.apply(this.postingOfUser,resp.data.data)
                    this.postingLoadCount++
                } else {
                    if(this.infinite_scroll.disabled === false)
                        alert(resp.data.msg)
                    this.infinite_scroll.disabled = true;
                }
            })
        },
        userView(targetUserId){
            if(common.verifyLogin()){
                localStorage.setItem("weibo_targetUserId", targetUserId)
                window.location.reload()
            }
            else {
                this.$router.push(common.base + '/login')
            }
        },detailView(id){
            localStorage.setItem("weibo_targetPostingId", id)
            this.$router.push(common.base + '/detail')
        },chatView(){
            this.$router.push(common.base + '/contact')
        }
        ,beforeAvatarUpload(file){
            if (file.type !== 'image/jpeg')
                return false
            else if (file.size / 1024 / 1024 > 2) {
                return false
            }
            this.userAlter.avatarAlter.data.type = file.type
            return true
        },afterAvatarUpload(){
            location.reload()
        },likeUserVisit(){
            common.TokenAxios({
                method:"post",
                url: common.BackEndIP + "user/like",
                params:{
                    sourceUserId: this.user.id,
                    targetUserId: this.userVisit.targetUser.id
                }
            }).then((resp)=>{
                console.log(resp)
                if(resp.data.code === "200"){
                    location.reload()
                }else {
                    this.notify('警告', resp.data.msg, 'warning')
                }})
        },unlikeUserVisit(){
            common.TokenAxios({
                method:"post",
                url: common.BackEndIP + "user/unlike",
                params:{
                    sourceUserId: this.user.id,
                    targetUserId: this.userVisit.targetUser.id
                }
            }).then((resp)=>{
                console.log(resp)
                if(resp.data.code === "200"){
                    location.reload()
                }else {
                    this.notify('警告', resp.data.msg, 'warning')
                }})
        },infiniteScroll(){
            const top = document.querySelector(".infinite-list").scrollTop
            if(top > 600) this.topBack = true
            else this.topBack = false
        },
        topBackScroll(){
            this.topBack = false;
            document.querySelector(".infinite-list").scrollTop = 0;
        },alterUser(){
            this.$refs['userForm'].validate((valid)=>{
                if(valid){
                    common.TokenAxios({
                        method:"post",
                        url: common.BackEndIP + "user/modify/account/" + this.user.id,
                        params: this.userAlter.accountAlter,
                    }).then((resp)=>{
                        console.log(resp)
                        if(resp.data.code === "200"){
                            location.reload()
                        }else {
                            this.notify('警告', resp.data.msg, 'warning')
                        }})}})}
        ,alterUserInfo(){
            this.$refs['userInfoForm'].validate((valid)=>{
                if(valid){
                    common.TokenAxios({
                        method:"post",
                        url: common.BackEndIP + "user/modify/info/" + this.user.id,
                        params: this.userAlter.infoAlter,
                    }).then((resp)=>{
                        console.log(resp)
                        if(resp.data.code === "200"){
                            location.reload()
                        }else {
                            this.notify('警告', resp.data.msg, 'warning')
                        }})}})
        },notify(title, msg,type) {
            this.$notify({
                title: title,
                message: msg,
                position: 'top-left',
                type: type,
            });
        },
    }
}
</script>

<style scoped>
.postingCSS{
    margin:20px 80px 20px 20px;
    height: auto;
    width: auto;
    background-color: #2c3e50;
    box-shadow: rgba(6, 24, 44, 0.4) 0px 0px 0px 2px, rgba(6, 24, 44, 0.65) 0px 4px 6px -1px, rgba(255, 255, 255, 0.08) 0px 1px 0px inset;
}
.infinite-list {
    overflow: auto;
    right: 0;
    height: calc(100% - 100px);
    margin-top: 100px;
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