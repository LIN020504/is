<template>
  <div id="body-table">

    <div class="header">
      <div>
        <p>Wang Mingzhi P33151 & Lin Peng P33111</p>
        <p>Stage_4</p>
      </div>
    </div>

    <div class="body">
      <div class="choose-area">

        <form id="hidden-form" style="display: none">
          <input type="text" id="pointX"/>
          <button type="submit" id="hidden-btn"></button>
        </form>

        <div>
          <form id="coordinates-form">
            <div class="checkbox-area">
              <a id="forXError" style="font-size: 10px; font-style: oblique; color: #4C489D"></a>
              <div id="x-group">
                <div class="row">
                  <input class="ordinary-btn" id="1" type="button" value="Nike" name="storehouse_id"
                         @click="rememberX(1),paintButtons(1)">
                  <input class="ordinary-btn" id="2" type="button" value="Adidas" name="storehouse_id"
                         @click="rememberX(2),paintButtons(2)">
                  <input class="ordinary-btn" id="3" type="button" value="LiNing" name="storehouse_id"
                         @click="rememberX(3),paintButtons(3)">
                </div>
              </div>


              <div class="buttons-area">
                <button id="function-btn" type="button" class="gradient-button" @click="validateInput()">
                  Buy
                </button>
              </div>
            </div>
          </form>
        </div>

      </div>
    </div>

    <div>
      <div class="button-row">
        <button type="button" class="gradient-button" @click="logout">LogOut</button>
        <button type="button" class="gradient-button" @click="deletePoints">Empty Shopping Cart</button>
      </div>
      <div class="table-area">
        <table id="main-table">
          <caption>Order</caption>
          <thead>
          <tr >
            <th class="col" id="col1">Storehouse</th>
            <th class="col" id="col1">Status Order</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="order in orders" v-bind:key="order.id">
            <td>{{ order.storehouse_id }}</td>
            <td>{{ order.status }}</td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>

  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "Main",
  data() {
    return {
      storehouse_id: "",
      status:"",
      orders: new Array(0)
    }
  },
  //侦听器，用于监听属性的变化并执行相应的操作

  methods: {
    addPoints() {
      axios.post('http://localhost:8083/api/points/main', {
        storehouse_id: document.getElementById('pointX').value
      }, {
        headers: {"Authorization": "Bearer " + localStorage.getItem("jwt")}
      }).then(() => {
        this.getPoints();
      }, () => {
        this.$swal.fire({
          icon: "error",
          text: "The order couldn't be added",
          title: ":("
        });
      }).catch(() => {
        this.$router.push({name: 'error-page-expired'})
      })
    },
    getPoints() {
      axios.get('http://localhost:8083/api/points/main', {
        headers: {"Authorization": "Bearer " + localStorage.getItem("jwt")}
      }).then((response) => {
        this.orders = response.data;
      }).catch((error) => {
        console.error('Error fetching points:', error); // 输出错误信息到控制台
        this.$router.push({name: 'error-page-expired'})
      })
    },

    deletePoints() {
      this.$swal.fire({
        title: ':?',
        text: "Can't recover",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
      }).then((result) => {
        if (result.isConfirmed) {
          axios.delete('http://localhost:8083/api/points/main', {
            headers: {"Authorization": "Bearer " + localStorage.getItem("jwt")}
          }).then(() => {
            this.getPoints();
            this.$swal.fire({
              icon: "success",
              text: "Orders removed",
              title: ":)"
            });
          }, () => {
            this.$swal.fire({
              icon: "error",
              text: "Failed to delete dots",
              title: ":("
            });
          }).catch(() => {
            this.$router.push({name: 'error-page-expired'})
          })}
      });
    },

    logout() {
      this.$swal.fire({
        title: ':?',
        text: "Sure to leave?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes',
      }).then((result) => {
        if (result.isConfirmed) {
          this.$swal.fire({
            icon: "success",
            text: "Bye",
            title: "Logout +ing",
          });
          this.$router.push({name: "auth-page"}, () => localStorage.clear());
        }
      });
    },


    rememberX(value1) {
      document.getElementById('pointX').value = value1;
    }
    ,

    validateInput() {
      if (document.getElementById('pointX').value !== '') {
        this.addPoints();
      } else {
        this.$swal.fire({
          icon: "error",
          text: "Didn't choose product",
          title: ":("
        });
      }
    },

    paintButtons(id) {
      let selected = document.querySelectorAll(".ordinary-btn-selected");
      if (selected !== null) {
        document.querySelectorAll(".ordinary-btn-selected").forEach(function (i) {
              i.classList.toggle("ordinary-btn");
              i.classList.toggle("ordinary-btn-selected");
            }
        )
      }
      document.getElementById(id).classList.toggle("ordinary-btn");
      document.getElementById(id).classList.toggle("ordinary-btn-selected");
    }
    ,
  }
  ,
  mounted() {
    this.getPoints();
  }
}
</script>

<style>
@import "../assets/header.css";
@import "../assets/body.css";
@import "../assets/adaptive.css";
</style>