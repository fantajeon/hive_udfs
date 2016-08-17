
package org.sch.udf;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;
 

public final class HangulEditDistance extends UDF {
  private static final char[] CHO = 
    /*ㄱ ㄲ ㄴ ㄷ ㄸ ㄹ ㅁ ㅂ ㅃ ㅅ ㅆ ㅇ ㅈ ㅉ ㅊ ㅋ ㅌ ㅍ ㅎ */
  {0x3131, 0x3132, 0x3134, 0x3137, 0x3138, 0x3139, 0x3141, 0x3142, 0x3143, 0x3145,
    0x3146, 0x3147, 0x3148, 0x3149, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};
  private static final char[] JUN = 
    /*ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ*/
  {0x314f, 0x3150, 0x3151, 0x3152, 0x3153, 0x3154, 0x3155, 0x3156, 0x3157, 0x3158,
    0x3159, 0x315a, 0x315b, 0x315c, 0x315d, 0x315e, 0x315f, 0x3160, 0x3161, 0x3162,
    0x3163};
    /*X ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ*/
  private static final char[] JON = 
  {0x0000, 0x3131, 0x3132, 0x3133, 0x3134, 0x3135, 0x3136, 0x3137, 0x3139, 0x313a,
    0x313b, 0x313c, 0x313d, 0x313e, 0x313f, 0x3140, 0x3141, 0x3142, 0x3144, 0x3145,
    0x3146, 0x3147, 0x3148, 0x314a, 0x314b, 0x314c, 0x314d, 0x314e};

  protected String splithangul(String s) {
    StringBuilder builder = new StringBuilder();
    System.out.printf("input string=%s\n", s);
    for(int i=0; i < s.length(); i++) {
      char uniVal = s.charAt(i);
      final char v = (char)(uniVal - 0xAC00);
      if( v >=0 && v <= 11172) {
        System.out.format("[%c:", uniVal);
        // 유니코드 표에 맞추어 초성 중성 종성을 분리합니다..
        char cho = (char) ((((v - (v % 28)) / 28) / 21));
        char jung = (char) ((((v - (v % 28)) / 28) % 21));
        char jong = (char) ((v % 28));
        builder.append(CHO[cho]);
        builder.append(JUN[jung]);
        if( jong != 0x0000 ) {
          builder.append(JON[jong]);
        }
        System.out.printf("%d,%d,%d]", (int)cho,(int)jung,(int)jong);
      } else {
        System.out.format("E");
        builder.append(uniVal);
      }
    }

    String result = builder.toString();
    System.out.printf("split string=%s\n", result);
    return result;
  }

  public final int edit_distance(final String s1, final String s2) {
    if (s1.equals(s2)) {
      return 0;
    }

    if (s1.length() == 0) {
      return s2.length();
    }

    if (s2.length() == 0) {
      return s1.length();
    }

    // create two work vectors of integer distances
    int[] v0 = new int[s2.length() + 1];
    int[] v1 = new int[s2.length() + 1];
    int[] vtemp;

    // initialize v0 (the previous row of distances)
    // this row is A[0][i]: edit distance for an empty s
    // the distance is just the number of characters to delete from t
    for (int i = 0; i < v0.length; i++) {
      v0[i] = i;
    }

    for (int i = 0; i < s1.length(); i++) {
      // calculate v1 (current row distances) from the previous row v0
      // first element of v1 is A[i+1][0]
      //   edit distance is delete (i+1) chars from s to match empty t
      v1[0] = i + 1;

      // use formula to fill in the rest of the row
      for (int j = 0; j < s2.length(); j++) {
        int cost = 1;
        if (s1.charAt(i) == s2.charAt(j)) {
            cost = 0;
        }
        v1[j + 1] = Math.min( v1[j] + 1,              // Cost of insertion
                      Math.min( v0[j + 1] + 1,  // Cost of remove
                          v0[j] + cost)); // Cost of substitution
      }

      // copy v1 (current row) to v0 (previous row) for next iteration
      //System.arraycopy(v1, 0, v0, 0, v0.length);

      // Flip references to current and previous row
      vtemp = v0;
      v0 = v1;
      v1 = vtemp;

    }

    return v0[s2.length()];
  }

  public double evaluate(final Text s, final Text d) {
    if( s== null && s == null) return 0;
    String str_s, str_d;
    if (s == null) { str_s = ""; }
    else {
      str_s = splithangul(s.toString());
    }
    if (d == null) { str_d = ""; }
    else {
      str_d = splithangul(d.toString());
    }
    return edit_distance(str_s, str_d);
  }
}

