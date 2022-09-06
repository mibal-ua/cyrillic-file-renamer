import ua.mibal.cyrillicFileRenamer.model.DynaStringArray;

import java.util.Arrays;

import static ua.mibal.cyrillicFileRenamer.model.Border.*;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class Test {

    public static void main(String[] arg) {
        String oldName = "сертификат-Для-ВПО-для-Балахона-М.А.";

        String[] borders = {HYPHENMINUS.getBorder(), ENDASH.getBorder(), EMDASH.getBorder(),
                MINUS.getBorder(), SPACE.getBorder(), UNDERSCORE.getBorder(), DOT.getBorder()};

        DynaStringArray dynaResult = new DynaStringArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < oldName.length(); i++) {
            boolean isThisABorder = false;
            char cha = oldName.charAt(i);
            String ch = String.valueOf(cha);
            for (final String border : borders) {
                if (ch.equals(border)) {
                    dynaResult.add(stringBuilder.append(ch).toString());
                    stringBuilder = new StringBuilder();
                    isThisABorder = true;
                }
            }
            if (!isThisABorder) {
                if ((i != (oldName.length() - 1)) && Character.isUpperCase(oldName.charAt(i + 1))) {
                    dynaResult.add(stringBuilder.append(ch).toString());
                    stringBuilder = new StringBuilder();
                } else {
                    stringBuilder.append(ch);
                }
            }

        }
        dynaResult.add(stringBuilder.toString());

        System.out.println(Arrays.toString(dynaResult.toArray()));
    }
}