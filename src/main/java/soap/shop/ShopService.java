package soap.shop;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import shop.VehicleDto;

import java.util.List;

@Service
public class ShopService {
    private static final String URL = "http://localhost:8090/bus/vehicles";
    private final WebClient client = WebClient.builder()
            .baseUrl(URL)
            .build();

    public VehicleDto getVehicle (long id) {
        return client.get()
                .uri("/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(VehicleDto.class)
                .onErrorResume(WebClientResponseException.class,
                        ex -> ex.getStatusCode() == HttpStatusCode.valueOf(404) ? Mono.empty() : Mono.error(ex))
                .block();
    }

    public List<VehicleDto> getVehiclesWithType (String type) {
        return client.get()
                .uri("?type=" + type)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<VehicleDto>>() {})
                .block();
    }

    public VehicleDto updateVehicle(VehicleDto vehicle) {
        return client.put()
                .uri("/" + vehicle.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(vehicle), VehicleDto.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<VehicleDto>() {})
                .block();
    }

    public void addWheels (VehicleDto vehicle, int wheels) {
        vehicle.setNumberOfWheels(vehicle.getNumberOfWheels() + wheels);
    }
}
