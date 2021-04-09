<template>
  <div>
    <h3>Welcome to the {{ title }} {{ version }}</h3>
    <div class="outer">
      
      <div class="inner">
      <h4 style="line-height: 50px;">Demonstration script</h4>
      <p>
        This application is for demonstration purpose, and used to create Vaccine orders
        as a governement agency may do to get vaccine lots.
      </p>
      <ul>
        <li>
          Go to the orders menu to see the list of current orders
        </li>
        <li>See the shipment plan for current shipment plans</li>
        <li>Using the new order button add a new vaccine request.</li>
        <li>See the shipment plan impacted by this new order</li>
      </ul>
      </div>
      <div>
      <p>This vaccine order microservice is represented as the purple component in the figure below. See explanations at <a href="https://ibm-cloud-architecture.github.io/vaccine-solution-main/solution/orderms/">this article</a></p>
      <v-img
          alt="IBM Cloud Logo"
          class="shrink mr-2"
          contain
          src="../assets/vaccine-order-1.png"
          transition="scale-transition"
          width="900px"
        />
      </div> 
    </div>
  </div>
</template>
<style scoped>
.outer {
  width: 100%;
  margin: 0 auto;
  line-height: 30px;
}

.inner {
  text-align: left;
  margin-left: 50px;
}
</style>

<script>
import axios from "axios";

export default {
  name: "Home",
  data: () => { 
      return {
        version: {},
        title: process.env.VUE_APP_TITLE 
      }
  },
  created() {
    this.initialize();
  },
  methods: {
    initialize() {
      axios.get("/api/v1/orders/version").then((resp) => (this.version = resp.data));
      console.log(this.version)
    }
  }
};
</script>
