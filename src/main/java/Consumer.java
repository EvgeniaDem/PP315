import org.example.dto.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.*;

public class Consumer {

    public static void main(String[] args) {
        // с помощью объекта resTemplate делаем запросы к удаленым сервисам
        RestTemplate restTemplate = new RestTemplate();

        String url = "http://94.198.50.185:7081/api/users";

        // получаем headers нашего http-запроса
        ResponseEntity<User[]> response = restTemplate.getForEntity(url, User[].class);
        User[] users = response.getBody();
        System.out.println("Users: " + Arrays.toString(users));

        String sessionId = getSessionId(response);

        // SAVE USER
        User user = new User("James", "Brown", 15);
        HttpHeaders headers = new HttpHeaders();
        headers.add(COOKIE, sessionId);

        // упаковываем JSON и header (он же cookie) в объект HttpEntity
        HttpEntity<User> request = new HttpEntity<>(user, headers);

        ResponseEntity<String> responseCode = restTemplate.postForEntity(url, request, String.class);
        System.out.println("ResponseCode: " + responseCode.getBody());


        // UPDATE USER
        HttpHeaders updateHeaders = new HttpHeaders();
        updateHeaders.add(COOKIE, sessionId);
        System.out.println("Headers: " + updateHeaders);

        User userToUpdate = new User(3L, "Thomas", "Shelby", 15);

        HttpEntity<User> updateRequest = new HttpEntity<>(userToUpdate, updateHeaders);
        ResponseEntity<String> updateResponse = restTemplate.exchange(url+"/2", HttpMethod.PUT, updateRequest, String.class);
        String updateCode = updateResponse.getBody();
        System.out.println("updateCode: "+ updateCode);


 /*       // DELETE USER
        HttpEntity<Map<Object, Object>> requestToDelete = new HttpEntity<>(jsonToUpdate, headers);
        ResponseEntity<String> responseEntityDelete = restTemplate.exchange(url,
                HttpMethod.DELETE,
                requestToDelete,
                String.class,
                params);
        String userDetailsDelete = responseEntityDelete.getBody();
        System.out.println(userDetailsDelete);*/
    }

    private static String getSessionId(ResponseEntity<?> response) {
        List<String> cookies = response.getHeaders().get(SET_COOKIE);
        System.out.println("Cookies: " + cookies);
        String sessionId = cookies == null ? null : cookies.stream()
                .filter(c -> c.startsWith("JSESSIONID"))
                .findFirst()
                //.map(s -> s.split(";")[0])
                .orElseThrow();
        System.out.println("sessionId: " + sessionId);
        return sessionId;
    }
}
