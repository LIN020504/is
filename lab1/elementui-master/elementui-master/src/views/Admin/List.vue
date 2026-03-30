<template>
  <div>
    <el-card class="box-card">
      <div slot="header" class="clearfix">
        <el-row>
          <el-button type="primary" size="mini"  @click="SearchClick">search</el-button>
          <el-button type="warning" size="mini"  @click="ResetClick">Clear</el-button>
          <el-button type="success" size="mini" @click="redirectToUserPerson">Go to Personal info</el-button>
          <el-button type="info" size="mini" @click="calculateAverageHeight">Avg Height</el-button>
          <el-button type="danger" size="mini" @click="calculateEyeColorPercentage">Eye Color %</el-button>
        </el-row>
      </div>
      <div>
        <el-form :inline="true" :model="searchForm" class="demo-form-inline">
          <el-form-item label="name">
            <el-input v-model="searchForm.Name" placeholder="Please enter your name"></el-input>
          </el-form-item>
          <el-form-item label="email">
            <el-input v-model="searchForm.Email" placeholder="Please enter your email address"></el-input>
          </el-form-item>
          <el-form-item label="telephone number">
            <el-input v-model="searchForm.PhoneNumber" placeholder="Please enter your mobile phone number"></el-input>
          </el-form-item>
          <el-form-item label="Birth" prop="Birth">
            <el-date-picker
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="Select Date"
                v-model="searchForm.Birth"
                clearable>
            </el-date-picker>
          </el-form-item>
          <el-form :inline="true" :model="searchForm" class="demo-form-inline">
            <el-form-item label="Eye Color" prop="EyeColor">
              <el-select v-model="searchForm.EyeColor" placeholder="Please select" clearable>
                <el-option key="GREEN" label="GREEN" value="GREEN"></el-option>
                <el-option key="BLACK" label="BLACK" value="BLACK"></el-option>
                <el-option key="BLUE" label="BLUE" value="BLUE"></el-option>
                <el-option key="ORANGE" label="ORANGE" value="ORANGE"></el-option>
                <el-option key="WHITE" label="WHITE" value="WHITE"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="Hair Color" prop="HairColor">
              <el-select v-model="searchForm.HairColor" placeholder="Please select" clearable>
                <el-option key="GREEN" label="GREEN" value="GREEN"></el-option>
                <el-option key="BLACK" label="BLACK" value="BLACK"></el-option>
                <el-option key="BLUE" label="BLUE" value="BLUE"></el-option>
                <el-option key="ORANGE" label="ORANGE" value="ORANGE"></el-option>
                <el-option key="WHITE" label="WHITE" value="WHITE"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="Nationality" prop="Nationality">
              <el-select v-model="searchForm.Nationality" placeholder="Please select" clearable>
                <el-option key="UNITED_KINGDOM" label="UNITED_KINGDOM" value="UNITED_KINGDOM"></el-option>
                <el-option key="USA" label="USA" value="USA"></el-option>
                <el-option key="THAILAND" label="THAILAND" value="THAILAND"></el-option>
              </el-select>
            </el-form-item>
          </el-form>

          <el-form-item label="Height" prop="Height">
            <el-input v-model.trim="searchForm.Height" placeholder="Please enter your height" clearable></el-input>
          </el-form-item>
        </el-form>
      </div>

      <PaginationTable ref="PaginationTableId" url="/User/List" :column="dataColum">
        <template v-slot:header>
          <el-button v-if="RoleType === 'admin'" type="primary" size="mini" icon="el-icon-edit" @click="ShowEditModal()">add</el-button>

          <ExportButton exportUrl="/User/Export" :where="searchForm"></ExportButton>
          <el-upload
              class="upload-demo"
              action="/User/import"
              :headers="headers"
              :show-file-list="false"
              :before-upload="beforeUpload"
              :on-success="handleImportSuccess"
              :on-error="handleImportError"
              :on-progress="handleProgress"
              :multiple="false"
              :limit="1"
              accept=".xls,.xlsx"
          >
            <el-button size="mini" type="primary" >
              <el-icon><Upload /></el-icon>
              Excel
            </el-button>
          </el-upload>

          <el-table :data="importHistory" border style="margin-top: 20px;">
            <el-table-column prop="Id" label="ID" width="80"></el-table-column>
            <el-table-column prop="Upload_time" label="Upload time"></el-table-column>
            <el-table-column prop="Status" label="Status">
              <template #default="{ row }">
                <el-tag :type="row.Status === '成功' ? 'success' : 'danger'">
                  {{ row.Status }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </template>

        <template v-slot:Operate="scope">
          <el-button type="primary" size="mini"  @click="ShowEditModal(scope.row.Id)" v-if="RoleType == 'admin'">modify</el-button>
          <el-button type="danger" size="mini"  @click="ShowDeleteModal(scope.row.Id)" v-if="RoleType == 'admin'">delete</el-button>
        </template>
      </PaginationTable>

      <el-dialog :title="formData.Id ? 'Modify User' : 'Add User'" :visible.sync="editorShow" width="80%" :lock-scroll="true"
                 min-height="800px">
        <el-form v-if="editorShow == true" ref="editModalForm" :rules="editModalFormRules" :model="formData"
                 label-width="140px" size="mini">

          <el-row :gutter="10">
            <el-form-item label="username" prop="UserName" placeholder="Please enter your account number">
              <el-input v-model.trim="formData.UserName" :disabled='formData.Id != null'></el-input>
            </el-form-item>

            <el-form-item label="password" prop="Password">
              <el-input type="password" v-model.trim="formData.Password"></el-input>
            </el-form-item>

            <el-form-item label="role" prop="RoleType">
              <SigleSelect v-model="formData.RoleType" url="/Select/RoleType" columnName="Name"
                           columnValue="Code" columnLabel="Label"></SigleSelect>
            </el-form-item>

            <el-form-item label="email" prop="Email" placeholder="Please enter your email address">
              <el-input v-model.trim="formData.Email"></el-input>
            </el-form-item>

            <el-form-item label="PhoneNumber" prop="PhoneNumber" placeholder="Please enter your phoneNumber">
              <el-input v-model.trim="formData.PhoneNumber"></el-input>
            </el-form-item>

            <el-form-item label="date of birth" prop="Birth" placeholder="Please enter the time of birth">
              <el-date-picker v-model="formData.Birth" align="right" type="date" placeholder="Select time of birth"
                              value-format="yyyy-MM-dd HH:mm:ss">
              </el-date-picker>
            </el-form-item>

            <el-form-item label="name" prop="Name" placeholder="Please enter your name">
              <el-input v-model.trim="formData.Name"></el-input>
            </el-form-item>

            <el-form :inline="true" :model="searchForm" class="demo-form-inline">
              <el-form-item label="Eye Color" prop="EyeColor">
                <el-select v-model="formData.EyeColor" placeholder="Please select" clearable>
                  <el-option key="GREEN" label="GREEN" value="GREEN"></el-option>
                  <el-option key="BLACK" label="BLACK" value="BLACK"></el-option>
                  <el-option key="BLUE" label="BLUE" value="BLUE"></el-option>
                  <el-option key="ORANGE" label="ORANGE" value="ORANGE"></el-option>
                  <el-option key="WHITE" label="WHITE" value="WHITE"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="Hair Color" prop="HairColor">
                <el-select v-model="formData.HairColor" placeholder="Please select" clearable>
                  <el-option key="GREEN" label="GREEN" value="GREEN"></el-option>
                  <el-option key="BLACK" label="BLACK" value="BLACK"></el-option>
                  <el-option key="BLUE" label="BLUE" value="BLUE"></el-option>
                  <el-option key="ORANGE" label="ORANGE" value="ORANGE"></el-option>
                  <el-option key="WHITE" label="WHITE" value="WHITE"></el-option>
                </el-select>
              </el-form-item>

              <el-form-item label="Nationality" prop="Nationality">
                <el-select v-model="formData.Nationality" placeholder="Please select" clearable>
                  <el-option key="UNITED_KINGDOM" label="UNITED_KINGDOM" value="UNITED_KINGDOM"></el-option>
                  <el-option key="USA" label="USA" value="USA"></el-option>
                  <el-option key="THAILAND" label="THAILAND" value="THAILAND"></el-option>
                </el-select>
              </el-form-item>
            </el-form>

            <el-form-item label="Height" prop="Height">
              <el-input v-model.trim="formData.Height" placeholder="Please enter your height" clearable></el-input>
            </el-form-item>
          </el-row>

          <el-row type="flex" justify="end" align="bottom">
            <el-form-item>
              <el-button type="primary" plain @click="CreateOrEditForm()">confirm</el-button>
              <el-button  @click="editorShow = false">Cancel</el-button>
            </el-form-item>
          </el-row>
        </el-form>
      </el-dialog>
    </el-card>
  </div>
</template>


<script>
import store from '@/store';
import { mapGetters } from 'vuex'
import { connectWS } from "@/ws.js";
import axios from 'axios'
import moment from 'moment'
export default {
  name: "UserList",

  mounted() {
    // 1. 建立 WebSocket 连接
    connectWS();

    // 2. 初始加载数据 (确保表格有数据)
    this.SearchClick();

    this.getImportHistory(); // 页面加载时获取历史记录
  },

  computed: {
    ...mapGetters([
      'Token', 'UserInfo', 'RoleType', 'HasUserInfo', 'ColumnType', "UserId",'getNeedsRefresh'
    ]),
    uploadHeaders() {
      return {
        Authorization: this.Token
      }
    }
  },
// UserList.vue watch 逻辑
  watch: {
    getNeedsRefresh(newVal) {
      if (newVal === true) {
        this.SearchClick()
        store.commit('SET_NEEDS_REFRESH', false); // 必须重置为 false
      }
    }
  },
  data() {
    return {
      importHistory: [],
      searchForm: {
        UserName: '',
        Email: '',
        Name: '',
        PhoneNumber: '',
        Birth: '',
        RoleTypeFormat: '',
        EyeColor: null,
        HairColor: null,
        Height: '',
        Nationality: null,
      },
      editorShow: false,
      dataColum: [
        {
          key: "Id",
          hidden: true,

        },
        {
          key: "RoleType",
          hidden: true,

        },
        {
          key: "UserName",
          title: "UserName",
          type: store.getters.ColumnType.SHORTTEXT,

        },

        {
          key: "Email",
          title: "Email",
          type: store.getters.ColumnType.SHORTTEXT,

        },
        {
          key: "Name",
          title: "Name",
          type: store.getters.ColumnType.SHORTTEXT,

        },
        {
          key: "PhoneNumber",
          title: "PhoneNumber",
          type: store.getters.ColumnType.PHONE,
        },
        {
          key: "Birth",
          title: "Birth",
          type: store.getters.ColumnType.DATE,

        },
        {
          key: "RoleTypeFormat",
          title: "RoleTypeFormat",
          type: store.getters.ColumnType.SHORTTEXT,

        },
        {
          key: "EyeColor",
          title: "EyeColor",
          type: store.getters.ColumnType.SHORTTEXT,

        },
        {
          key: "HairColor",
          title: "HairColor",
          type: store.getters.ColumnType.SHORTTEXT,

        },
        {
          key: "Height",
          title: "Height",
          type: store.getters.ColumnType.SHORTTEXT,
        },
        {
          key: "Nationality",
          title: "Nationality",
          type: store.getters.ColumnType.SHORTTEXT,
        },
        {
          title: "Operate",
          width:"200px",
          key: "Operate",
          type: store.getters.ColumnType.USERDEFINED,
        },
      ],
      formData: {
      },
      editModalFormRules: {
        "UserName": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],
        "Password": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],
        "Email": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],
        "ImageUrls": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],
        "Name": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],

        "PhoneNumber": [
          { required: true, message: 'This field is required', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              var reg = /^1[34578]\d{9}$/;
              if (!value || !reg.test(value)) {
                callback(new Error('Please enter the correct mobile phone number'));
              }
              else {
                callback();
              }
            }, trigger: 'blur'
          },
        ],
        "Birth": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],
        "RoleType": [
          { required: true, message: 'This field is required', trigger: 'blur' },
        ],
        "EyeColor": [{ required: true, message: 'Please select an eye color', trigger: 'change' }],
        "HairColor": [{ required: true, message: 'Please select a hair color', trigger: 'change' }],
        "Height": [{ required: true, message: 'Please enter your height', trigger: 'blur' }],
        "Nationality": [{ required: true, message: 'Please select your nationality', trigger: 'change' }]
      },

      listLoading: false,
      importDialogVisible: false,
    }
  },
  created() {
    this.initColumns();
  },

  methods: {
    initColumns() {
      // 基础列
      this.dataColum = [
        {key: "Id", hidden: true},
        {key: "RoleType", hidden: true},
        {key: "UserName", title: "UserName", type: store.getters.ColumnType.SHORTTEXT},
        {key: "Email", title: "Email", type: store.getters.ColumnType.SHORTTEXT},
        {key: "Name", title: "Name", type: store.getters.ColumnType.SHORTTEXT},
        {key: "PhoneNumber", title: "PhoneNumber", type: store.getters.ColumnType.PHONE},
        {key: "Birth", title: "Birth", type: store.getters.ColumnType.DATE},
        {key: "RoleTypeFormat", title: "RoleTypeFormat", type: store.getters.ColumnType.SHORTTEXT},
        {key: "EyeColor", title: "EyeColor", type: store.getters.ColumnType.SHORTTEXT},
        {key: "HairColor", title: "HairColor", type: store.getters.ColumnType.SHORTTEXT},
        {key: "Height", title: "Height", type: store.getters.ColumnType.SHORTTEXT},
        {key: "Nationality", title: "Nationality", type: store.getters.ColumnType.SHORTTEXT},
      ];

      // 仅当 RoleType 为 admin 时才添加 Operate 列
      if (this.RoleType === 'admin') {
        this.dataColum.push({
          title: "Operate",
          width: "200px",
          key: "Operate",
          type: store.getters.ColumnType.USERDEFINED,
        });
      }
    },
    openImportDialog() {
      this.importDialogVisible = true
    },

    submitImport() {
      this.$refs.userImport.submit()
    },

    async handleImportSuccess() {
      // 检查响应是否为对象
      if (response && typeof response === 'object') {
        console.log('响应对象详情:', response);

        // 关键：后端返回的是 Msg，不是 Message
        // 优先使用 Msg，如果不存在则使用 Message
        const message = response.Msg || response.Message || '未知错误';

        // 检查成功标志（注意大小写）
        const success = response.Success || response.success || false;
        const code = response.Code || response.code || '';

        if (success === true) {
          // 真正的成功
          this.$message.success(message);

          // 刷新数据
          this.$refs.PaginationTableId.Reload(this.searchForm);
          await this.getImportHistory();

        } else {
          // 失败（包括空Excel文件的情况）
          console.log('上传失败，错误代码:', code, '错误信息:', message);

          // 根据错误代码显示不同的消息
          let displayMessage = message;

          if (code === '500' || message.includes('Excel') || message.includes('文件')) {
            // 这是Excel文件相关的错误
            displayMessage = `文件导入失败: ${message}`;
          }

          this.$message.error({
            message: displayMessage,
            duration: 5000 // 显示5秒
          });

          // 即使失败也刷新导入历史，查看失败记录
          await this.getImportHistory();
        }
      } else {
        // 响应格式异常
        this.$message.error('服务器响应格式异常');
      }
      //
      // this.$message.success("Import completed successfully")
      // this.importDialogVisible = false
      // this.$refs.PaginationTableId.Reload(this.searchForm);
      // await this.getImportHistory();
    },

    async handleImportError(err) {
      this.$message.error("Import failed")
      console.error(err)
      await this.getImportHistory();
    },


    /**
     * 点击新增或者编辑的时候会触发
     */
    async ShowEditModal(Id) {
      let {Data} = await this.$Post(`/User/Get`, {Id: Id});
      console.log("API 返回的 Data:", Data);
      this.formData = Data || {};  // 确保 formData 不为空对象
      console.log("赋值后的 formData:", this.formData);
      this.editorShow = true;
    },


    /**
     * 点击保存的时候会触发
     */
    async CreateOrEditForm() {
      this.$refs.editModalForm.validate(async valid => {
        if (valid) {
          var {Success} = await this.$Post(`/User/CreateOrEdit`, this.formData);
          if (Success) {
            this.editorShow = false;
            this.$refs.PaginationTableId.Reload(this.searchForm);
          }
        }
      })
    },

    async SearchClick() {
      console.log('searchForm:', this.searchForm); // 打印 searchForm
      this.$refs.PaginationTableId.Reload(this.searchForm);
    },
    /**
     * 点击清空表单会触发
     */
    async ResetClick() {
      Object.keys(this.searchForm).forEach(k => this.searchForm[k] = '');
      this.$refs.PaginationTableId.Reload(this.searchForm);
    },
    /**
     * 单个删除的时候会触发
     */
    async ShowDeleteModal(Id) {
      await this.$PostDelete(`/User/Delete`, {Id: Id});
      this.$refs.PaginationTableId.Reload(this.searchForm);
    },

    /**
     * 跳转到 UserPerson 页面
     */
    redirectToUserPerson() {
      this.$router.push({path: "/Admin/UserPerson"});
    },

    async calculateAverageHeight() {
      try {
        let {Data} = await this.$Post("/User/CalculateAverageHeight", this.searchForm);
        this.$message.info(`Average Height: ${Data}`);
      } catch (error) {
        this.$message.error("Failed to calculate average height");
      }
    },

    async calculateEyeColorPercentage() {
      if (!this.searchForm.EyeColor) {
        this.$message.warning("Please select an eye color first!");
        return;
      }

      try {
        const response = await this.$Post("/User/CalculateEyeColorPercentage", {
          EyeColor: this.searchForm.EyeColor
        });

        console.log("Full API response:", response);

        // 假设 response = { Data: { Message, Data, Success } }
        const percentage = response.Data.Data; // 注意这里取两层 Data

        this.$message.success(
            `Eye color ${this.searchForm.EyeColor}: ${percentage}% of total users`
        );
      } catch (error) {
        this.$message.error("Failed to calculate percentage");
      }
    },

    async getImportHistory() {
      try {
        const res = await axios.get("/User/importHistory");
        console.log("从后端获取的数据:", res.data);

        if (res.data && res.data.Success && res.data.Data) {
          this.importHistory = res.data.Data; // 确保赋值的是正确的数组
        } else {
          this.importHistory = []; // 防止赋值错误
        }
      } catch (error) {
        console.error("获取导入历史失败:", error);
        this.importHistory = [];
      }
    },


  }
}
</script>
<style scoped></style>