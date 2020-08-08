package group.study.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class DemoApplication {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	// TODO: 2020-08-05 그리고 그것도 변경해야됨(현재 브랜치에 다가 마스터 브랜치 엎어치기). 근데 엎어치기 전에 위에까지 한 작업들 리모트에 푸시해서 복제본 남겨두고 하자
	// TODO: 2020-08-06 product detail 페이지 작업
	// TODO: 2020-08-06 회원가입에서 아이디 중복으로 실패하거나 무튼 실패하면 500 에러로 그냥 떨어짐 이거 고쳐야됨
	// TODO: 2020-08-06 JPA에서 DB에 auto_increment를 따라하게 설정하니(identity) 회원가입이 email 중복으로 인해 실패하면 다음에 성공해도 pk가 1증가된 상태임
	// TODO: 2020-08-09 로그인된 사용자의 정보(이름, 프사?)를 header에서 보여주도록 변경(스키마랑 entity 모두 변경
	// TODO: 2020-08-09 카테고리 선택했을 때, css 적용되게 main.html 수정(선택된 카테고리가 색이 변하게?, 당근마켓 참조)
}