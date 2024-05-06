
<template>
    <div class="toolbar">
        <div style="margin-right: 60px">
            <div @click="userView(user.id)">
                <el-image :src="user.avatar" style="height: 40px;width: 40px; border-radius: 20px">
                </el-image>
            </div>
            <div style="text-align: center" @click="userView(user.id)">{{user.nickname}}</div>
        </div>
    </div>
    <div style="margin-top: 100px; height: calc(100% - 100px);width: 100%">
        <div style="width: 20%;height: 80px;background-color: #2c3e50;">
            <el-row style="height: 40%"><el-text style="margin: auto; font-size: 18px;" >联系人</el-text></el-row>
            <el-row style="height: 50%;width: 100%;">
                <div style="color: white;margin: auto;font-size: 24px">
                    <el-icon @click="searchChat.visible=true" v-if="searchChat.visible==false"><Search /></el-icon>
                </div>
                <el-input @change="chatSearch"  v-model="searchChat.content" style="margin:auto;width: 80%; color: black" v-if="searchChat.visible==true"></el-input>
            </el-row>
        </div>
        <div style="width: 20%;height: calc(100% - 80px);background-color: cadetblue;">
            <div infinite-scroll-distance="10"  v-infinite-scroll="loadFriends" :infinite-scroll-immediate="false" :infinite-scroll-disabled="friendLoad.disabled" class="infinite-list" >
            <div  v-for="(item, index) in friends" :key="index" style="margin:auto;width: 80%; background-color: cornflowerblue;border-radius: 10px">
                <el-row style="margin-top: 10px">
                <el-col :span="6" v-if="item.roomName==null" style="font-size: 36px;color: white;"><el-icon><ChatRound /></el-icon></el-col>
                <el-col :span="6" v-if="item.roomName!=null" style="font-size: 36px;color: white;"><el-icon><ChatLineRound /></el-icon></el-col>
                <el-col @click="loadChatRecord(1,null,item.sourceUserId, true)" v-if="item.roomName==null && item.targetUserId==user.id"  :span="18" style="margin: auto;">{{item.nickname}}</el-col>
                    <el-col @click="loadChatRecord(1,null,item.targetUserId, true)" v-if="item.roomName==null && item.sourceUserId==user.id"  :span="18" style="margin: auto;">{{item.nickname}}</el-col>
                <el-col @click="loadChatRecord(1,item.roomName,null, true)" v-if="item.roomName!=null" :span="18" style="margin: auto;">{{item.roomName}}</el-col>
                </el-row>
            </div>
            </div>
        </div>
        <div style="width: 80%;height:100%;top:100px;right:0;background-color:wheat;position: fixed;">
            <div style="width: 80%;height:100%;overflow: auto">

                <div style="width: 100%;height:60%;">
                    <div infinite-scroll-distance="10"  v-infinite-scroll="()=>loadChatRecord(chatRecordLoad.page+1,chatRecordLoad.roomName,chatRecordLoad.targetUserId, false)" :infinite-scroll-immediate="false" :infinite-scroll-disabled="chatRecordLoad.disabled" class="infinite-list" >
                    <el-row  v-for="(item, index) in chatRecord" :key="index" style="font-size:16px;margin-top: 10px;margin-left: 10px">
                        <div style=" width: auto;overflow: auto;max-width: 60%;background-color: #66ccff;border-radius: 20px">
                            <div style="padding: 10px;min-width:180px ">
                                <el-row>
                                <el-col :span="4">
                                    <el-row>
                                    <el-image :src="item.auth.avatar" style="width: 30px;height:30px;border-radius: 10px;margin:auto"></el-image>
                                    </el-row>
                                    <el-row >
                                        <el-text style="margin: auto;overflow: auto">{{item.auth.nickname}}</el-text>
                                    </el-row>
                                </el-col>
                                <el-col :span="20" style="overflow: auto">
                                <el-text>{{item.record.content}}</el-text>
                                </el-col></el-row>
                            </div>
                        </div>
                    </el-row>
                    </div>
                </div>
                <div style=" width: 100%;height:40%;">
                    <div class="chatInput" style="width: 100%;height: 80%;">
                    <el-input
                        @change="uploadChat"
                        style="width: 100%;height: 80%;"
                        v-model="chatUpload"
                        type="textarea"
                        placeholder="Please input"
                        resize="none"                   >
                    </el-input>
                    </div>
                </div>
            </div>
            <div style="width: 16%;position:fixed;height:100%;right: 0;top:100px;">
                <el-table :data="memberList" stripe style="width: 100%;height: calc(50% - 40px);">
                    <el-table-column prop="avatar" label="头像" style="height: auto;" >
                    <el-image :src="memberList.avatar"></el-image>
                    </el-table-column>
                    <el-table-column prop="nickname" label="昵称" style="height: auto;overflow: auto;" />
                </el-table>
                <el-table :data="memberList" stripe style="width: 100%;height: calc(50% - 40px);">
                    <el-table-column prop="avatar" label="头像" style="height: auto;" >
                    <el-image :src="memberList.avatar"></el-image>
                    </el-table-column>
                    <el-table-column prop="nickname" label="昵称" style="height: auto;overflow: auto;" />
                </el-table>
            </div>
        </div>
    </div>

</template>

<script>
import common from "@/components/common.vue";
let echo_websocket = null

export default {
    name: "ContactView",
    data(){
        return{
            chatUpload:"",
            friendLoad:false,
            chatRecordLoad:{
                disabled:false,
                page:1,
                roomName:null,
                targetUserId:null
            },
            memberList:[
                {
                    avatar: "",
                    nickname:'4444444444444444444444444444444',
                }
                ],
            liveList:[],
            searchChat:{
              visible:false,
              content:""
            },
            user:{
                id:-1,
                avatar:"",
                nickname:"",
            },
            friends:[
                {
                    sourceUserId:0,
                    roomName:"",
                    targetUserId:0,
                    nickname: "",
                    create_time:"",
                }
            ],chatRecord:[
                {
                    record:{
                        content: ""
                    },
                    auth:{
                        nickname: "",
                        avatar: "http://127.0.0.1:9000/weibo-avatar/default.jpg"
                    }
                }
            ]
        }
    },mounted() {
        if(common.verifyLogin()){
            this.user.avatar = localStorage.getItem("weibo_avatar")
            this.user.nickname = localStorage.getItem("weibo_nickname")
            this.user.id = localStorage.getItem("weibo_userId")
        }
        common.TokenAxios({
            method:"get",
            url:common.BackEndIP + "chat/friend",
            params:{
                userId:this.user.id,
                page:1
            }
        }).then((resp)=>{
            console.log(resp)
            if (resp.data.code==="200"){
                this.friends = resp.data.data
            }
        })
        let url = common.wsUri +"ws/chat?" + this.user.id;
        //创建WebSocket客户端对象
        echo_websocket = new WebSocket(url);
        //开门握手完成回调
        echo_websocket.onopen = (evt)=> {
            console.log(evt);
        };
        //3.监听服务端的消息
        echo_websocket.onmessage = (evt)=> {
            this.chatRecord.$set(0,JSON.parse(evt.data))
            console.log("接收服务端消息:" + evt.data);
        };
        //4.如果连接中断
        echo_websocket.onerror = (evt)=> {
            console.log('ERROR:'+evt.data+'');
            //关闭连接
            echo_websocket.close()
        };
        //5.注册close事件
        echo_websocket.onclose = (evt)=>{
            console.log('INFO：关闭连接');
            if(evt.reason){
                console.log(`错误信息：${evt.reason}`);
            }
        }
    },methods:{
        userView(targetUserId){
            if(common.verifyLogin()){
                localStorage.setItem("weibo_targetUserId", targetUserId)
                window.location.reload()
            }
            else {
                this.$router.push(common.base + '/login')
            }
        },chatSearch(){
            this.searchChat.visible = false
        },loadChatRecord(page, roomName,targetUserId, change){
            if (change){this.chatRecordLoad.disabled=false}
            if((roomName!=null || targetUserId!=null) && !this.chatRecordLoad.disabled){
                common.TokenAxios({
                    method: "get",
                    url:common.BackEndIP+"chat/record",
                    params:{
                        page:page,
                        roomName:roomName,
                        userId:this.user.id,
                        targetUserId:targetUserId
                    }
                }).then((resp)=>{
                    if (resp.data.code === "200"){
                        this.chatRecordLoad.page = page;
                        this.chatRecordLoad.roomName = roomName
                        this.chatRecordLoad.targetUserId = targetUserId
                        this.chatRecord = resp.data.data
                    }else {
                        this.chatRecordLoad.disabled = true
                        if(!this.chatRecordLoad.disabled)
                            alert(resp.data.msg)
                    }
                })
            }
        },loadFriends(){

        },
        uploadChat(){
            if(this.chatRecordLoad.roomName != null)
            {
                echo_websocket.send("sourceUserId="+this.user.id+"&targetRoomName="+ this.chatRecordLoad.roomName +"&content=" + this.chatUpload);
                this.chatRecord.unshift( {
                    record:{
                        content: this.chatUpload,
                    },
                    auth:{
                        nickname: this.user.nickname,
                        avatar: this.user.avatar
                    }
                });
            }
            if(this.chatRecordLoad.targetUserId != null){
                echo_websocket.send("sourceUserId="+this.user.id+"&targetUserId="+ this.chatRecordLoad.targetUserId +"&content=" + this.chatUpload);
                this.chatRecord.unshift({
                    record:{
                        content: this.chatUpload,
                    },
                    auth:{
                        nickname: this.user.nickname,
                        avatar: this.user.avatar
                    }
                });
            }
            this.chatUpload = ""
        }
    }
}
</script>

<style scoped>
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
.infinite-list {
    overflow: auto;
    right: 0;
    height: 100%;
    list-style: none;
}
.chatInput /deep/ .el-textarea__inner {
    height: 100%;
    width: 100%;
}
</style>