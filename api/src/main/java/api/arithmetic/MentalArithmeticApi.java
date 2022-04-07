package api.arithmetic;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "mental-arithmetic", url = "http://localhost:8086/mental-arithmetic/")
public interface MentalArithmeticApi {

    @GetMapping("/random/{level}")
    ArithmeticExpression random(@PathVariable("level") Integer level);

}
