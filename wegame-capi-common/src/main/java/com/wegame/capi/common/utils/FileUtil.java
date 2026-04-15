package com.wegame.capi.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wegame.base.utils.FastJsonUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileUtil {

    public static String readClasspathFile(String path) throws Exception {
        ClassPathResource resource = new ClassPathResource(path);
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }

    public static String getRealJsonInfo(String jsonRootName, String configInfo) {
        JSONObject root = JSON.parseObject(configInfo);
        return Objects.nonNull(root) ? FastJsonUtils.toJSONString(root.getJSONObject(jsonRootName)) : null;
    }

}

