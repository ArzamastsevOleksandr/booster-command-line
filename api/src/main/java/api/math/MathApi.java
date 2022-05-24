package api.math;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "math", url = "http://localhost:8081/math/")
public interface MathApi {

    @GetMapping("/random/{level}")
    ArithmeticExpression random(@PathVariable("level") Integer level);

}
