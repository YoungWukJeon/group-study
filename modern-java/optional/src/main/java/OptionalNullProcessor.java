import model.optional.Account;
import model.optional.Posts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OptionalNullProcessor {
    public List<String> findTitleByAccountAvoidNullUsingOptionalMap(List<Posts> posts, Account account) {
        return posts.stream()
                .filter(p -> p.getAccount()
//                                .map(Account::getUid)
//                                .filter(uid -> uid.equals(account.getUid()))
//                                .map(uid -> true)
                                .map(o -> o.getUid().equals(account.getUid()))
                                .orElse(false)
                ).map(Posts::getTitle)
                .collect(Collectors.toList());
    }

    public List<String> findTitleByAccountAvoidNullUsingOptionalFlatMap(List<Posts> posts, Account account) {
        return posts.stream()
                .filter(p -> p.getAccount()
                        .flatMap(o -> Optional.ofNullable(o.getUid().equals(account.getUid())))
                        .get()
                ).map(Posts::getTitle)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        OptionalNullProcessor main = new OptionalNullProcessor();

        Account account = Account.builder().uid(1L).name("hong").build();
        List<Posts> posts = List.of(
                Posts.builder().account(Optional.ofNullable(account)).title("title1").createAt(LocalDateTime.now()).build(),
                Posts.builder().account(Optional.empty()).title("title2").createAt(LocalDateTime.now()).build(),
                Posts.builder().account(Optional.ofNullable(account)).title("title3").createAt(LocalDateTime.now()).build()
        );
        List<String> titles = main.findTitleByAccountAvoidNullUsingOptionalFlatMap(posts, account);
        System.out.println(titles);
    }
}
