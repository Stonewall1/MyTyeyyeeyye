import api.Response;
import api.Result;
import com.google.gson.Gson;
import example.MyUser;
import example.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws IOException {
        //1 Получение данных
        Supplier<Response> supplier = Main::getUser;
        Response response0 = supplier.get();
        List<Result> results = response0.getResults();

        //2 Функция преобразования T -> R
        Function<Result, MyUser> function = r -> new MyUser(r.getGender(),r.getRegistered().getDate(),
                                                            r.getName().getFirst(),r.getName().getLast(),
                                                            r.getRegistered().getAge());

        Stream<MyUser> MyNewStream = results.stream().map(function);

        //3 Правило для фильтрации и т.д.
        Predicate<MyUser> predicate = user -> user.getAge() > 18 & user.getAge() < 62;

        //Logic here:
        List<MyUser> X = MyNewStream
                .filter(predicate)
                .sorted(Comparator.comparing(MyUser::getGender)
                        .thenComparing(MyUser::getRegDate)
                        .thenComparing(MyUser::getFirstName)
                        .thenComparing(MyUser::getLastName)
                        .thenComparing(MyUser::getLastName)
                        .thenComparing(MyUser::getAge))
                        .toList();

        //4 Сохранение определенной операции
        Consumer<MyUser> consumer = System.out::println;
        X.forEach(consumer);


    }


    public static Response getUser() {
        String uri = "https://randomuser.me/api/?results=1000";
        //TODO make different methods
        //String uri = "?results=10&noinfo";
        //String uri = "?gender=female";

        //String uri = "?format=csv";
        //JSON (default), PrettyJSON or pretty, CSV, YAML, XML
        //String uri = "?nat=gb(,fi)"
        //v1.3: AU, BR, CA, CH, DE, DK, ES, FI, FR, GB, IE, IR, NO, NL, NZ, TR, US
        //String uri = "?results=5&inc=name,gender,nat& noinfo";
        //String uri = "?inc=gender(,name)";
        //String uri = "?exc=login";
        String get = "GET";
        URL url;
        HttpURLConnection con;
        BufferedReader in;
        StringBuilder content = new StringBuilder();
        Gson gson = new Gson();
        Response response;
        try {
            url = new URL(uri);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(get);
            con.getResponseCode();
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            response = gson.fromJson(content.toString(), Response.class);
            in.close();
            con.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
