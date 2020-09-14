package group.study.demo.common.converter;

import group.study.demo.persistence.entity.ProductEntity;
import io.r2dbc.spi.Row;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDateTime;

@ReadingConverter
public class ProductEntityReadConverter implements Converter<Row, ProductEntity> {
    @Override
    public ProductEntity convert(Row source) {
        return ProductEntity.builder()
                .no(source.get("no", Long.class))
                .name(source.get("name", String.class))
                .category(source.get("category", String.class))
                .description(source.get("description", String.class))
                .price(source.get("price", Long.class))
                .image(source.get("image", String.class))
                .createDate(source.get("create_date", LocalDateTime.class))
                .updateDate(source.get("update_date", LocalDateTime.class))
                .build();
    }
}