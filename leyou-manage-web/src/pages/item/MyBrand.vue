<template>
  <div>
    <v-layout class="px-3 pb-2">
      <v-flex xs2>
        <v-btn  color="info" @click="addBrand">新增品牌</v-btn>
      </v-flex>
      <v-spacer/>
      <v-flex xs4>
        <v-text-field label="搜索" hide-details v-model="search" append-icon="search"></v-text-field>
      </v-flex>
    </v-layout>

    <v-data-table
      :headers="headers"
      :items="brands"
      :pagination.sync="pagination"
      :server-items-length="totalBrands"
      :loading="loading"
      class="elevation-1"
    >
      <template slot="items" slot-scope="props">
        <td class="text-xs-center">{{ props.item.id }}</td>
        <td class="text-xs-center">{{ props.item.name }}</td>
        <td class="text-xs-center"><img v-if="props.item.image" :src="props.item.image" width="130" height="40"/></td>
        <td class="text-xs-center">{{ props.item.letter }}</td>
        <td class="text-xs-center">
          <v-btn flat icon color="info">
            <v-icon>edit</v-icon>
          </v-btn>
          <v-btn flat icon color="error">
            <v-icon>delete</v-icon>
          </v-btn>
        </td>
      </template>
    </v-data-table>
    <v-dialog max-width="500" v-model="show" presistent>
      <v-card>
        <!--对话框标题-->
        <v-toolbar dense dark color="primary">
          <v-toolbat-title>新增品牌</v-toolbat-title>
          <v-spacer/>
          <v-btn icon @click="closeWindow"><v-icon>close</v-icon></v-btn>
        </v-toolbar>
        <!--对话框的内容-->
        <v-card-text class="px-5">
            我是表单
        </v-card-text>
      </v-card>

    </v-dialog>
  </div>
</template>

<script>

  export default {
    components: {
      VIcon,
      VBtn,
      VToolbarSideIcon,
      VCard,
      VDialog,
      VSpec},
    name: "myBrand",
    data () {
      return {
        totalBrands: 0,
        brands: [], //当前页品牌数据
        loading: false,  //是否显示加载
        pagination: {},
        headers: [
          {text: 'id', align: 'center', value: 'id'},
          {text: '名称', align: 'center', value: 'name', sortable: false},
          {text: 'LOGO', align: 'center', value: 'image', sortable: false},
          {text: '首字母', align: 'center', value: 'letter'},
          {text: '操作', align: 'center', sortable:true}
        ],

        search: "",//查询关键字
        show:false
      }
    },
    created(){
      this.brands=[
        {id:1, name:"小米", image:"", letter:"X"},
        {id:2, name:"苹果", image:"", letter:"P"},
        {id:3, name:"小米", image:"", letter:"X"},
        {id:4, name:"苹果", image:"", letter:"P"},
        {id:5, name:"小米", image:"", letter:"X"},
        {id:6, name:"苹果", image:"", letter:"P"},
        {id:7, name:"三星", image:"", letter:"S"}
      ];
      this.totalBrands=15;
      this.loadBrand();

    },
    watch:{
      search(){
        this.loadBrand();
      },
      pagination:{
        deep:true,
        handler(){
          this.loadBrand();
        }
      }
    },
    methods:{
      loadBrand(){
        this.$http.get("/item/brand/page",{
          params: {
            page: this.pagination.page, // 当前页
            rows: this.pagination.rowsPerPage, // 每页条数
            sortBy: this.pagination.sortBy, // 排序字段
            desc: this.pagination.descending, // 是否降序
            key:this.search,
          }
        })
      },
      addBrand(){
        //控制弹窗可见
        this.show=true;
      },
      closeWindow(){
        this.show=false;
      }
    }
  }
</script>

<!--当前样式只作用于当前组件的节点-->
<style scoped="">

</style>
