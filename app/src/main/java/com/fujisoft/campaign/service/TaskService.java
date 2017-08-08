package com.fujisoft.campaign.service;

import com.fujisoft.campaign.bean.MTaskData;
import com.fujisoft.campaign.utils.Constants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by 860115007 on 2017/1/25.
 * task网络访问服务
 */
public interface TaskService {
    /**
     * 任务大厅
     *
     * @param userId
     * @param pageNum
     * @param keyword
     * @return
     */
    @GET(Constants.TASK_INDEX)
    Call<MTaskData> taskIndex(
            @Query("id") String userId//用户id
            , @Query("pageNum") int pageNum//分页
            , @Query("keyword") String keyword);//关键字

    /**
     * 已完成任务
     *
     * @param userId
     * @param pageNum
     * @param keyword
     * @return
     */
    @GET(Constants.TASK_FINISHED_LIST)
    Call<MTaskData> taskFinished(
            @Query("id") String userId//用户id
            , @Query("pageNum") int pageNum//分页
            , @Query("keyword") String keyword);//关键字
}
