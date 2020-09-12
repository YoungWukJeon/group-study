package tacos.web.api;

import lombok.Data;

import java.util.List;

@Data
public class EmailOrder {
    private String email;
    private List<EmailTaco> tacos;

    @Data
    public static class EmailTaco {
        private String name;
        private List<String> ingredients;
    }
}
