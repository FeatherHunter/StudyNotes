<template>
  <div style="margin-top: 20px;">
    <!--工具栏-->
    <el-col :span="24" class="toorbar">
      <!--<div class="grid-content bg-purple-dark" style="height:100px;background:pink;"></div>-->
      <el-form :inline="true" :model="formInline" class="demo-form-inline" style="height:45px;margin-top:6px;">
        <el-form-item>
          <el-input v-model="formInline.user" placeholder="姓名"></el-input>
        </el-form-item>
        <el-form-item style="font-size:14px;">
          <el-button type="primary" @click="onSubmit">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onAdd">新增</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onEdit">编辑</el-button>
        </el-form-item>
      </el-form>
    </el-col>


    <!--table-->
    <el-table
      ref="multipleTable"
      :data="tableData3"
      tooltip-effect="dark"
      style="width: 100%;"
      :default-sort="{prop: 'age', order: 'descending'}"
      @selection-change="handleSelectionChange">
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column
        label="日期"
        width="180">
        <template slot-scope="scope">{{ scope.row.date }}</template>
      </el-table-column>
      <el-table-column
        prop="name"
        label="姓名"
        width="150">
      </el-table-column>
      <el-table-column
        prop="sex"
        label="性别"
        width="130" sortable>
      </el-table-column>
      <el-table-column
        prop="age"
        label="年龄"
        width="130" sortable>
      </el-table-column>
      <el-table-column
        prop="address"
        label="地址"
        show-overflow-tooltip>
      </el-table-column>
    </el-table>


    <!--工具条-->
    <el-col :span="24" class="toolbardel">
      <el-button type="danger" @click="batchRemove">批量删除</el-button>
      <el-pagination layout="prev, pager, next" @current-change="handleCurrentChange" :page-size="20" :total="50"
                     style="float:right;">
      </el-pagination>
    </el-col>
  </div>
</template>

<script>
  export default {
    name: "table",
    data() {
      return {
        tableData3: [{
          date: '2016-05-03',
          name: '王小虎',
          sex: '女',
          age: '13',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-02',
          name: '王小虎',
          sex: '女',
          age: '36',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-04',
          name: '王小虎',
          sex: '男',
          age: '36',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-01',
          name: '王小虎',
          sex: '男',
          age: '40',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-08',
          name: '王小虎',
          sex: '女',
          age: '24',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-06',
          name: '王小虎',
          sex: '男',
          age: '33',
          address: '上海市普陀区金沙江路 1518 弄'
        }, {
          date: '2016-05-07',
          name: '王小虎',
          sex: '女',
          age: '29',
          address: '上海市普陀区金沙江路 1518 弄'
        }],
        multipleSelection: [],

        formInline: {
          user: '',
          region: ''
        }
      }
    },
    methods: {
      toggleSelection(rows) {
        if (rows) {
          rows.forEach(row => {
            this.$refs.multipleTable.toggleRowSelection(row);
          });
        } else {
          this.$refs.multipleTable.clearSelection();
        }
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      onSubmit() {
        console.log('submit!');
      }
    }
  }
</script>

<style scoped>
  .toorbar {
    background: pink;
    text-align: left;
    padding-left: 10px;
    height: 52px;
    /*display: flex;*/
    /*display: -webkit-flex;*/
    /*align-items: center;*/
  }

  .toolbardel {
    margin-top: 20px;
  }
</style>
