public class StringMatchExample {

    public static void main(String args[]) {
        String[] alphabets = {"", "a2345", "A12345", "b2345B",
                                  "12345a" , "abcd" , "aa343"};
     
        for(String alphabet : alphabets) {
           System.out.println(" does " + alphabet +
             " contains alphabetic words : " + alphabet.matches(".*[A-Za-z].*"));

        }
     
        //checking if String contains digits or not
        String[] numbers = {"1234" , "+1234", "234a"};
        for(String number : numbers) {
           System.out.println(" number " + number + " contains only 1-9 digits : "
               + number.matches(".*[1-9].*"));
        }
        System.out.println("substring:"+alphabets[1].substring(1,alphabets[1].length()-1)); 
    	System.out.println("tamanho:"+alphabets[1].length()); 
    }
}

