package co.mapoteca.controller;

import co.mapoteca.dto.point.CreatePointDto;
import co.mapoteca.dto.point.PointDto;
import co.mapoteca.dto.point.PointSummary;
import co.mapoteca.dto.response.PointData;
import co.mapoteca.service.PointService;
import co.mapoteca.service.PointUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

//todo endpoint para procesar un punto
@Tag(name = "Points")
@RestController
@RequestMapping("/points")
@CrossOrigin("*")
public class PointController {

    private final PointService pointService;
    private final PointUpdateService pointUpdateService;

    public PointController(PointService pointService, PointUpdateService pointUpdateService) {
        this.pointService = pointService;
        this.pointUpdateService = pointUpdateService;
    }


    @PostMapping
    public ResponseEntity<String> buyPoints(
            @RequestParam UUID organizationId,
            @RequestBody List<CreatePointDto> createPointList
    ) throws Exception {
        String redirectUrl = pointService.save(organizationId, createPointList);
        return ResponseEntity.ok(redirectUrl);
    }

    @GetMapping("/validate-payment")
    @Operation(summary = "Validar pago de puntos",
               description = "Valida la compra de un punto, y genera su información.")
    public ResponseEntity<PointData> validatePointsPayment(
//            HttpServletRequest request,
            @RequestParam String transactionId) throws Exception {
        PointData pointData = pointUpdateService.updateStatus(transactionId);
        return ResponseEntity.ok(pointData);
    }

    @GetMapping("/re-new-information")
    @Operation(summary = "Renovar información de puntos",
               description = "Renueva la información de los puntos de una organización, esto borra la información previa y genera una nueva a la fecha.")
    public ResponseEntity<PointData> reNewInformation(
            HttpServletRequest request,
            @RequestParam UUID organizationId) throws Exception {
        PointData pointData = pointService.reNewPointInformation(request, organizationId);
        return ResponseEntity.ok(pointData);
    }

    @PostMapping("/process")
    @Operation(summary = "Procesar un punto pagado",
               description = "Captura la información de un punto que se encuentre pagado")
    public ResponseEntity<String> processPointInformation(
            HttpServletRequest request,
            @RequestParam UUID dateId) throws Exception {
        pointService.processPointInformation(request, dateId);
        return ResponseEntity.ok("Punto capturado");
    }

    @GetMapping("/paid-points")
    public ResponseEntity<List<PointDto>> getPaidPoints(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(pointService.getPaidPointsByOrganization(organizationId));
    }

    @GetMapping("/paid-points/summaries")
    public ResponseEntity<List<PointSummary>> getPaidPointsSummaries(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(pointService.getPaidPointsSummaries(organizationId));
    }

    @GetMapping("/pending-points")
    @Operation(summary = "Puntos en estado pendiente",
               description = "Verifica los puntos que no han sido pagados. y/o que no se encuentran procesados.")
    public ResponseEntity<List<PointDto>> getPendingPoints(@RequestParam UUID organizationId) {
        return ResponseEntity.ok(pointService.getPendingPointsByOrganization(organizationId));
    }

    @DeleteMapping
    public ResponseEntity<?> deletePaidPoint(@RequestParam UUID pointId) throws Exception {
        pointService.deletePaidPoint(pointId);
        return ResponseEntity.ok("Point deleted");
    }

   /* @GetMapping
    public ResponseEntity<?> testAwsApi(HttpServletRequest request) throws Exception {
        pointService.getPointInformation(
                request,
                Point.builder()
                        .latitude(9.042624552419033)
                        .longitude(-79.43699618325384)
                        .address("AVenida")
                        .organizationId(UUID.randomUUID())
                        .range(300)
                        .build()
                );
        return ResponseEntity.ok("");
    }
*/
}
