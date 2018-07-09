package com.mycoin.data;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface InterfaceAdapter {

    // 注册
    @POST("api/signup/")
    Call<Register> getRegisterInfo(@Body RegisterUser user);

    // 查重并发邮件
    @POST("api/signup/unique/")
    Call<Check> getCheckMsg(@Body CheckUser user);

    // 验证码注册
    @POST("api/signup/captcha/")
    Call<Check> getCodeMsg(@Body CodeUser user);

    // 登陆
    @POST("api/signin/")
    Call<Login> getUserToken(@Body LoginUser user);

    // 添加预算
    @POST("api/budget/")
    Call<AddBudget> getAddBudget(@Body AddBudgetUser addBudgetUser, @Header("token")String token);

    // 查看预算
    @POST("api/view_budget/")
    Call<AddBudget> getUserBudget(@Body BudgetUser budgetUser, @Header("token")String token);

    // 查看一个月的总和
    @GET("api/get_one/{month}/")
    Call<Cost> getCost(@Path("month")int month, @Header("token")String token);

    // 查姓名
    @GET("api/show_profile/")
    Call<Profile> getProfile(@Header("token")String token);

    // 添加账单
    @POST("api/add_account/")
    Call<Accounting> getAccounting(@Body AccountingUser accountingUser, @Header("token")String token);

}
