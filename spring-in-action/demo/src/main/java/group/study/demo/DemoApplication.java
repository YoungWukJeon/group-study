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
	// TODO: 2020-08-06 로그아웃 구현, 그리고...product detail 페이지 작업, 로그인되면 header에 버튼들 바뀌게 수정
	// TODO: 2020-08-06 회원가입에서 아이디 중복으로 실패하거나 무튼 실패하면 500 에러로 그냥 떨어짐 이거 고쳐야됨
	// TODO: 2020-08-05 그리고 그것도 변경해야됨(현재 브랜치에 다가 마스터 브랜치 엎어치기). 근데 엎어치기 전에 위에까지 한 작업들 리모트에 푸시해서 복제본 남겨두고 하자
	// TODO: 2020-08-06 JPA에서 DB에 auto_increment를 따라하게 설정하니(identity) 회원가입이 emial 중복으로 인해 실패하면 다음에 성공해도 pk가 1증가된 상태임
}
