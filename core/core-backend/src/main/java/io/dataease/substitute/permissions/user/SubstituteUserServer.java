package io.dataease.substitute.permissions.user;


import io.dataease.api.permissions.user.vo.UserFormVO;
import io.dataease.auth.xtoken.AuthUserContext;
import io.dataease.auth.xtoken.User;
import io.dataease.utils.IPUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Component
@ConditionalOnMissingBean(name = "loginServer")
@RestController
@RequestMapping("/user")
public class SubstituteUserServer {

    @GetMapping("/info")
    public Map<String, Object> info() {
        User user = AuthUserContext.get();
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("oid", user.getUserBid());
        result.put("language", "zh-CN");
        return result;
    }
    @GetMapping("/personInfo")
    public UserFormVO personInfo() {
        UserFormVO userFormVO = new UserFormVO();
        userFormVO.setId(1L);
        userFormVO.setAccount("admin");
        userFormVO.setName("管理员");
        userFormVO.setIp(IPUtils.get());
        // 当前模式为无XPack
        userFormVO.setModel("lose");
        return userFormVO;
    }
}
