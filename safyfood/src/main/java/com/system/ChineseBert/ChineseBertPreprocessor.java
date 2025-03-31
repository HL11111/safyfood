package com.system.ChineseBert;

import java.util.List;

public class ChineseBertPreprocessor {
//    private HfTokenizer tokenizer;
//    private NDManager manager;
//
//    public ChineseBertPreprocessor() {
//        tokenizer = HfTokenizer.newInstance("bert-base-chinese");
//        manager = NDManager.newBaseManager();
//    }
//
//    public NDArray preprocess(String text) {
//        List<Integer> inputIds = tokenizer.tokenize(text).getInputIds();
//        // 添加CLS和SEP标记
//        inputIds.add(0, 101);
//        inputIds.add(102);
//        // 创建输入张量
//        NDArray inputArray = manager.create(inputIds).toType(NDArray.Type.INT32, false);
//        return inputArray;
//    }
}
