import model.Account;
import model.Posts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OriginalNullProcessor {
    public List<String> findTitleByAccount(List<Posts> posts, Account account) {
        return posts.stream()
                .filter(p -> p.getAccount().getUid().equals(account.getUid()))
                .map(Posts::getTitle)
                .collect(Collectors.toList());
    }

    public List<String> findTitleByAccountAvoidNull(List<Posts> posts, Account account) {
        return posts.stream()
                .filter(p -> {
                    if (p.getAccount() != null) {
                        Account pAccount = p.getAccount();
                        if (pAccount.getUid() != null) {
                            Long uid = pAccount.getUid();
                            if (account != null && account.getUid() != null) {
                                return uid.equals(account.getUid());
                            }
                            return false;
                        }
                        return false;
                    }
                    return false;
                })
                .map(Posts::getTitle)
                .collect(Collectors.toList());
    }

    public List<String> findTitleByAccountAvoidNullUsingManyExit(List<Posts> posts, Account account) {
        return posts.stream()
                .filter(p -> {
                    if (p.getAccount() == null) {
                        return false;
                    }
                    Account pAccount = p.getAccount();
                    if (pAccount.getUid() == null) {
                        return false;
                    }
                    Long uid = pAccount.getUid();
                    if (account == null || account.getUid() == null) {
                        return false;
                    }
                    return uid.equals(account.getUid());
                })
                .map(Posts::getTitle)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        OriginalNullProcessor main = new OriginalNullProcessor();

        Account account = Account.builder().uid(1L).name("hong").build();
        List<Posts> posts = List.of(
                Posts.builder().account(account).title("title1").createAt(LocalDateTime.now()).build(),
                Posts.builder().title("title2").createAt(LocalDateTime.now()).build(),
                Posts.builder().account(account).title("title3").createAt(LocalDateTime.now()).build()
        );
        List<String> titles = main.findTitleByAccountAvoidNullUsingManyExit(posts, account);
        System.out.println(titles);
    }
}
