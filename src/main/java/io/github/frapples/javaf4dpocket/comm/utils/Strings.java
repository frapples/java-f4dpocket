package io.github.frapples.javaf4dpocket.comm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.constraints.NotNull;

/**
 * @author Frapples <isfrapples@outlook.com>
 * @date 2019/11/8
 */
public class Strings {

    public static List<MatchResult> regMatch(@NotNull String str, @NotNull Pattern pattern) {
        Matcher m = pattern.matcher(str);
        ArrayList<MatchResult> list = new ArrayList<>();
        if (m.find()) {
            do {
                list.add(m.toMatchResult());
            } while (m.find());
            return list;
        } else {
            return list;
        }
    }
}
