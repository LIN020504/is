<template>
    <el-button size="mini" type="success" icon="el-icon-download" @click="ExportClick()">Export</el-button>
</template>
<script>
export default {

    props: {
        exportUrl: {
            type: String, //导出地址
            default: "",
        },
        where: {//搜索属性
            type: Object,
            default: () => { }
        },
    },

    methods: {
        /**
         * 导出
         */
        async ExportClick() {
          const query = encodeURIComponent(JSON.stringify(this.where))
          window.open(`${this.exportUrl}?query=${query}`, '_blank')
        },
    }
}

</script>