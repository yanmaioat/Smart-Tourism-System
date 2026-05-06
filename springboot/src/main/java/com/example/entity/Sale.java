package com.example.entity;


import java.math.BigDecimal;

public class Sale {


    private Integer id; // 订单ID


    private String orderNo; // 订单编号

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    private String name; // 车辆名称


    private String trainImg; // 车辆图片


    private BigDecimal trainPrice; // 车票价格


    private String trainTime; // 发车时间


    private Integer trainId; // 车票ID


    private Integer num; // 购买数量

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    private BigDecimal total; // 购买总价


    private Integer userId; // 下单人ID


    private String time; // 下单时间


    private String payNo; // 支付单号


    private String payTime; // 支付时间

    private String status; // 订单状态

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
// Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrainImg() {
        return trainImg;
    }

    public void setTrainImg(String trainImg) {
        this.trainImg = trainImg;
    }

    public BigDecimal getTrainPrice() {
        return trainPrice;
    }

    public void setTrainPrice(BigDecimal trainPrice) {
        this.trainPrice = trainPrice;
    }

    public String getTrainTime() {
        return trainTime;
    }

    public void setTrainTime(String trainTime) {
        this.trainTime = trainTime;
    }

    public Integer getTrainId() {
        return trainId;
    }

    public void setTrainId(Integer trainId) {
        this.trainId = trainId;
    }




    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPayNo() {
        return payNo;
    }

    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
