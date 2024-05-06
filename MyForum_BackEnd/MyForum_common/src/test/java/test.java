import com.weibo.common.constants.HttpCodeEnum;
import org.springframework.beans.factory.support.SimpleInstantiationStrategy;

import java.util.HashSet;
import java.util.List;


public class test {
    public static void main(String[] args) {
        String s1 = new StringBuilder().append("think").append("123").toString();
        System.out.println(s1.intern() == s1);
        String s3 = new StringBuilder().append("think").append("123").toString();
        System.out.println(s3.intern() == s3);
        String s2 = new StringBuilder().append("ja").append("va").toString();
        System.out.println(s2.intern() == s2);

    }
}
