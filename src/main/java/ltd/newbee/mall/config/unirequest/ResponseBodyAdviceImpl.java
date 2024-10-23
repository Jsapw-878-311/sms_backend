//package ltd.newbee.mall.config.unirequest;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.SneakyThrows;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.MethodParameter;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
//
//@RestControllerAdvice
//public class ResponseBodyAdviceImpl implements ResponseBodyAdvice<Object> {
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    /**
//     * 是否支持advice功能 true支持
//     * @param returnType
//     * @param converterType
//     * @return
//     */
//    @Override
//    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
//        return true;
//    }
//
//    @SneakyThrows
//    @Override
//    public Object beforeBodyWrite(Object body,
//                                  MethodParameter returnType,
//                                  MediaType selectedContentType,
//                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
//                                  ServerHttpRequest request,
//                                  ServerHttpResponse response) {
//        if(body instanceof String){
//            try {
//                return objectMapper.writeValueAsString(ResultData.success(body));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        }
//        if(body instanceof ResultData){
//            return body;
//        }
//        return ResultData.success(body);
//    }
//}
