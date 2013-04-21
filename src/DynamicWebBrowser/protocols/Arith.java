package DynamicWebBrowser.protocols;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Steffen, Mark, Shane
 */
public class Arith implements Protocol {

	@Override
	public String execute(URI uri) {
            String response = "";
            String answer = uri.getSchemeSpecificPart();
            Pattern pat = Pattern.compile("^(\\d+[.]*\\d*)([+]|[-]|[*]|[/]){1}(\\d+[.]*\\d*)$");
            Matcher matcher = pat.matcher(answer);
            
            response += "<HTML><HEAD><TITLE>Arithmetic</TITLE></HEAD>\n";
            response += "<BODY><H1>Arith is implemented. <br> <br>";
            if(matcher.matches())
            {
                response += "num1 = " + matcher.group(1) + "<br> operand = " + matcher.group(2) + "<br> num2 = " + matcher.group(3);
                if(matcher.group(2).compareTo("+") == 0)
                {
                    double result = 0;
                    result = Double.parseDouble(matcher.group(1)) + Double.parseDouble(matcher.group(3));
                    response += "<br> ANSWER = " + result + "</H1></BODY></HTML>";
                }
                if(matcher.group(2).compareTo("-") == 0)
                {
                    double result = 0;
                    result = Double.parseDouble(matcher.group(1)) - Double.parseDouble(matcher.group(3));
                    response += "<br> ANSWER = " + result + "</H1></BODY></HTML>";
                }
                if(matcher.group(2).compareTo("*") == 0)
                {
                    double result = 0;
                    result = Double.parseDouble(matcher.group(1)) * Double.parseDouble(matcher.group(3));
                    response += "<br> ANSWER = " + result + "</H1></BODY></HTML>";
                }
                if(matcher.group(2).compareTo("/") == 0)
                {
                    double result = 0;
                    result = Double.parseDouble(matcher.group(1)) / Double.parseDouble(matcher.group(3));
                    response += "<br> ANSWER = " + result + "</H1></BODY></HTML>";
                }
            }
            else
            {
                response += "The URI provided does not match the format [NUMBER1] + or - or * or / [NUMBER2]." + " </H1></BODY></HTML>";
            }
            return response;
	}
}