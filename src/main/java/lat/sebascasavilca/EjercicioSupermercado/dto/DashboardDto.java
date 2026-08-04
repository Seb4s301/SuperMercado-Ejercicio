package lat.sebascasavilca.EjercicioSupermercado.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDto {
    private long totalProductos;
    private long totalSucursales;
    private long totalVentas;
    private Double ingresoTotal;
    private List<VentasPorSucursal> ventasPorSucursal;
    private List<ProductoVendido> productosMasVendidos;
    private List<VentasPorMes> ventasPorMes;

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VentasPorSucursal {
        private String nombreSucursal;
        private long cantidadVentas;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProductoVendido {
        private String nombreProducto;
        private long unidadesVendidas;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VentasPorMes {
        private String mes;
        private long cantidadVentas;
        private Double ingreso;
    }
}
