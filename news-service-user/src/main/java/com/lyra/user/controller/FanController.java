package com.lyra.user.controller;

import com.lyra.api.user.controller.BaseController;
import com.lyra.api.user.controller.FansControllerAPI;
import com.lyra.exception.GraceException;
import com.lyra.pojo.vo.FansSexCountVO;
import com.lyra.pojo.vo.RegionsVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.user.service.FanService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FanController extends BaseController implements FansControllerAPI {
    @Autowired
    private FanService fanService;

    @Override
    public GraceJSONResult isMeFollowThisWriter(String writerId, String fanId) {
        // 0. 判断传入参数是否合法
        if (StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)) {
            GraceException.display(ResponseStatusEnum.USERNAME_NOTFOUND);
        }

        // 1. 根据userId和fanId统计条数 若大于0 则表示已关注
        Boolean result = fanService.queryFanCountByUserId(writerId, fanId);

        return GraceJSONResult.ok(result);
    }

    @Override
    public GraceJSONResult follow(String writerId, String fanId) {
        // 0. 判断参数是否合法
        if (StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)) {

            GraceException.display(ResponseStatusEnum.USERNAME_NOTFOUND);
        }

        fanService.addFans(writerId, fanId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult unfollow(String writerId, String fanId) {
        if (StringUtils.isBlank(writerId) || StringUtils.isBlank(fanId)) {

            GraceException.display(ResponseStatusEnum.USERNAME_NOTFOUND);
        }

        fanService.deleteFans(writerId, fanId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String writerId, Integer page, Integer pageSize) {
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = fanService.findFansListByPage(writerId, page, pageSize);

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(String writerId) {
        if (StringUtils.isBlank(writerId)) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        List<RegionsVO> regionsVOS = fanService.queryRegionsCount(writerId);

        return GraceJSONResult.ok(regionsVOS);
    }

    @Override
    public GraceJSONResult queryRatio(String writerId) {
        if (StringUtils.isBlank(writerId)) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        FansSexCountVO fansSexCountVO =
                new FansSexCountVO();

        fansSexCountVO.setManCounts(fanService.queryFanManCount(writerId));
        fansSexCountVO.setWomanCounts(fanService.queryFanWomanCount(writerId));

        return GraceJSONResult.ok(fansSexCountVO);
    }
}
