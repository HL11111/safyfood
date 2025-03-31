package com.system.ChineseBert;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

public class ChineseBertController {
//    private final ChineseBertService chineseBertService;
//    private final ChineseBertPreprocessor chineseBertPreprocessor;
//
//    public ChineseBertController(ChineseBertService chineseBertService, ChineseBertPreprocessor chineseBertPreprocessor) {
//        this.chineseBertService = chineseBertService;
//        this.chineseBertPreprocessor = chineseBertPreprocessor;
//    }
//
//    @PostMapping("/predict")
//    public ResponseEntity<NDList> predict(@RequestBody String text) {
//        NDArray inputArray = chineseBertPreprocessor.preprocess(text);
//        NDList result = chineseBertService.predict(inputArray);
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }
}
