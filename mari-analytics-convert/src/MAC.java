import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mari Analytics Convertor.
 */
public class MAC {

	public MAC() {		
	}

	private String digitsOnly(String in) {
		if(in!=null) {
			String result="";
			for(int i=0; i<in.length(); i++) {
				if(Character.isDigit(in.charAt(i))) {
					result+=in.charAt(i);
				}
			}
			return result;
		} else {
			return null;
		}
			
	}

	private void extractMax(Map<String,String> idToLine, Map<String,Integer> idToHits, PrintWriter out) {
		int max = -1;
		String maxkey = "...";
		String maxurl = "...";
		if(idToLine.size()>0) {
			for(String k:idToHits.keySet()) {
				if(idToHits.get(k)>max) {
					max = idToHits.get(k);
					maxkey = k;
					maxurl = idToLine.get(k);
				}
			}
		}
		out.println("  <tr><td>"+max+"</td><td>"+maxkey+"</td><td><a target=\"_blank\" href=\"http://www.rozhlas.cz"+maxurl+"\">"+maxurl+"</a></td>");
		if (maxkey!=null) {
			idToHits.remove(maxkey);
		}
	}
	
	private void convert(String from) throws IOException {
		String to = from+".html";
		System.out.println("Converting "+from+" to "+to);

		String[] lines = readLines(from);
		if(lines!=null) {
			Map<String,String> idToLine = new HashMap<>();
			Map<String,Integer> idToHits = new HashMap<>();
			Map<String,Integer> idToUnique = new HashMap<>();
			// Stránka,Zobrazení stránek,Unikátní zobrazení stránek
			for(String l:lines) {
				if(l.startsWith("/")) {
					System.out.println(l);
					String[] split = l.split(",");
					String url = split[0];
					if(Character.isDigit(url.charAt(url.length()-1))) {
						String[] urlSplit = url.split("--");
						String id = digitsOnly(urlSplit[urlSplit.length-1]);
						String hits = digitsOnly(split[1]);			
						String unique = digitsOnly(split[2]);
						
						System.out.println("  "+id+" '"+hits+"' '"+unique+"'");
						
						idToLine.put(id, url);
						if(idToHits.containsKey(id)) {
							idToHits.put(id, idToHits.get(id)+Integer.parseInt(hits));
						} else {
							idToHits.put(id, Integer.parseInt(hits));							
						}
						
						// TODO idToUnique.put(id, Integer.parseInt(unique));						
					} else {
						System.out.println("  SKIPPING");
					}
				}
			}
			
			System.out.println("Result:");
			PrintWriter out = new PrintWriter(to);
			out.println("<html><body><table border=\"1\">");
			out.println("<tr><td>Hits</td><td>ID</td><td>URL</td>");
			while(idToHits.size()>0) {
				extractMax(idToLine, idToHits, out);				
			}
			out.println("</table></body></html>");
		}
	}
	
    public String[] readLines(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }
    
	public static void main(String[] args) throws IOException {		
		MAC mac =  new MAC();
		if(args!=null && args.length==1) {
			mac.convert(args[0]);			
		} else {
			System.out.println("Add path to input CSV file as parameter");
		}
	}
}
