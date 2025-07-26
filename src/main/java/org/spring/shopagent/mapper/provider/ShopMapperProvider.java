package org.spring.shopagent.mapper.provider;

import lombok.RequiredArgsConstructor;
import org.spring.shopagent.exception.CustomException;
import org.spring.shopagent.exception.ErrorType;
import org.spring.shopagent.mapper.ShopMapper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShopMapperProvider {

    private final ApplicationContext ctx;

    // 의존성 주입을 사용할 수 없기때문에 ApplicationContext를 통해 ShopMapper를 가져오는 메서드
    public ShopMapper get() {
        try {
            return ctx.getBean(ShopMapper.class);
        } catch (BeansException e) {
            throw new CustomException(ErrorType.DB_CONNECTION_ERROR);
        }
    }
}