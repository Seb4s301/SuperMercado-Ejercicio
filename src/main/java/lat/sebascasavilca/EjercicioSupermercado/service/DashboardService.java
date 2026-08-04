package lat.sebascasavilca.EjercicioSupermercado.service;

import lat.sebascasavilca.EjercicioSupermercado.dto.DashboardDto;
import lat.sebascasavilca.EjercicioSupermercado.model.DetalleVenta;
import lat.sebascasavilca.EjercicioSupermercado.model.Producto;
import lat.sebascasavilca.EjercicioSupermercado.model.Venta;
import lat.sebascasavilca.EjercicioSupermercado.repository.ProductoRepository;
import lat.sebascasavilca.EjercicioSupermercado.repository.SucursalRepository;
import lat.sebascasavilca.EjercicioSupermercado.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService implements IDashboardService {

    @Autowired
    private VentaRepository ventaRepo;

    @Autowired
    private ProductoRepository productoRepo;

    @Autowired
    private SucursalRepository sucursalRepo;

    @Override
    public DashboardDto obtenerEstadisticas() {
        List<Venta> ventas = ventaRepo.findAllWithDetails();

        long totalProductos = productoRepo.count();
        long totalSucursales = sucursalRepo.count();
        long totalVentas = ventas.size();

        Double ingresoTotal = ventas.stream()
                .map(Venta::getTotal)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);

        List<DashboardDto.VentasPorSucursal> ventasPorSucursal = ventas.stream()
                .filter(v -> v.getSucursal() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getSucursal().getNombre(),
                        Collectors.counting()))
                .entrySet().stream()
                .map(e -> DashboardDto.VentasPorSucursal.builder()
                        .nombreSucursal(e.getKey())
                        .cantidadVentas(e.getValue())
                        .build())
                .collect(Collectors.toList());

        Map<String, Integer> productoUnidades = new LinkedHashMap<>();
        for (Venta v : ventas) {
            for (DetalleVenta d : v.getDetalle()) {
                if (d.getProd() != null) {
                    productoUnidades.merge(
                            d.getProd().getNombre(),
                            d.getCantProd(),
                            Integer::sum);
                }
            }
        }
        List<DashboardDto.ProductoVendido> productosMasVendidos = productoUnidades.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .map(e -> DashboardDto.ProductoVendido.builder()
                        .nombreProducto(e.getKey())
                        .unidadesVendidas(e.getValue())
                        .build())
                .collect(Collectors.toList());

        DateTimeFormatter fmtMes = DateTimeFormatter.ofPattern("yyyy-MM");
        Map<String, List<Venta>> ventasPorMes = ventas.stream()
                .filter(v -> v.getFecha() != null)
                .collect(Collectors.groupingBy(v -> v.getFecha().format(fmtMes)));

        List<DashboardDto.VentasPorMes> ventasMes = ventasPorMes.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Double ingreso = e.getValue().stream()
                            .map(Venta::getTotal)
                            .filter(Objects::nonNull)
                            .reduce(0.0, Double::sum);
                    return DashboardDto.VentasPorMes.builder()
                            .mes(e.getKey())
                            .cantidadVentas(e.getValue().size())
                            .ingreso(ingreso)
                            .build();
                })
                .collect(Collectors.toList());

        return DashboardDto.builder()
                .totalProductos(totalProductos)
                .totalSucursales(totalSucursales)
                .totalVentas(totalVentas)
                .ingresoTotal(ingresoTotal)
                .ventasPorSucursal(ventasPorSucursal)
                .productosMasVendidos(productosMasVendidos)
                .ventasPorMes(ventasMes)
                .build();
    }
}
