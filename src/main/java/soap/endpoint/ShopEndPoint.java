package soap.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import shop.*;
import soap.exception.ServiceFaultException;
import soap.model.VehicleType;
import soap.shop.ShopService;

import java.util.List;

@Endpoint
public class ShopEndPoint {
    private static final String NAMESPACE_URI = "http://shop";

    private final ShopService shopService;

    @Autowired
    public ShopEndPoint (ShopService shopService) {
        this.shopService = shopService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "AddWheelsRequest")
    @ResponsePayload
    public AddWheelsResponse addWheels (@RequestPayload AddWheelsRequest request) {
        VehicleDto vehicle = shopService.getVehicle(request.getId());
        if (vehicle == null) {
            ServiceStatus fault = new ServiceStatus();
            fault.setStatusCode("NOT_FOUND");
            fault.setMessage("Vehicle with id: " + request.getId() + " is not found!");
            throw new ServiceFaultException("Non-existent id", fault);
        }
        shopService.addWheels(vehicle, request.getWheels());

        AddWheelsResponse response = new AddWheelsResponse();
        response.setVehicle(shopService.updateVehicle(vehicle));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SearchByTypeRequest")
    @ResponsePayload
    public SearchByTypeResponse searchByType (@RequestPayload SearchByTypeRequest request) {
        if (!checkIfType(request.getType())) {
            ServiceStatus fault = new ServiceStatus();
            fault.setStatusCode("BAD_REQUEST");
            fault.setMessage("Type: " + request.getType() + " is not found!");
            throw new ServiceFaultException("Non-existent type", fault);
        }

        SearchByTypeResponse response = new SearchByTypeResponse();
        List<VehicleDto> vehicles = shopService.getVehiclesWithType(request.getType());
        response.getVehicles().addAll(vehicles);
        return response;
    }

    boolean checkIfType (String message) {
        for (VehicleType type: VehicleType.values()) {
            if (type.name().equals(message)) {
                return true;
            }
        }
        return false;
    }
}
