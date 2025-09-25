package app;

import java.math.BigDecimal;

public class FinanzasPersonales {

    public static void main(String[] args) {
        System.out.println(" GESTOR DE FINANZAS PERSONALES \n");

        try {
            // Inicializar servicios
            GestorUsuarios gestorUsuarios = new GestorUsuarios();
            GestorCategorias gestorCategorias = new GestorCategorias();
            GestorCuentas gestorCuentas = new GestorCuentas();
            CalculadoraBalances calculadora = new CalculadoraBalances();
            GestorTransacciones gestorTransacciones = new GestorTransacciones(calculadora);

            //  PRUEBA 1: GESTIÓN DE USUARIOS 
            System.out.println("1. CREANDO USUARIOS");
            Usuario usuario1 = gestorUsuarios.registrarUsuario("Andrés Ignacio Murcia Corredor", "ignacio@email.com", "123456");
            Usuario usuario2 = gestorUsuarios.registrarUsuario("Tomás Henao Bonilla", "tomas@email.com", "abcdef");

            System.out.println("Usuario creado: " + usuario1);
            System.out.println("Usuario creado: " + usuario2);

            // Autenticación
            System.out.println("Autenticación usuario1: " + gestorUsuarios.autenticarUsuario(usuario1.getId(), "123456"));
            System.out.println("Autenticación usuario2 por email: " + gestorUsuarios.autenticarUsuarioPorEmail("maria@email.com", "abcdef"));

            //  PRUEBA 2: GESTIÓN DE CATEGORÍAS 
            System.out.println("\n2. GESTIÓN DE CATEGORÍAS");
            gestorCategorias.agregarCategoria("Gimnasio", TipoCategoria.GASTO);
            gestorCategorias.agregarCategoria("Dividendos", TipoCategoria.INGRESO);

            System.out.println("Categorías de INGRESO:");
            gestorCategorias.obtenerCategoriasPorTipo(TipoCategoria.INGRESO)
                    .forEach(c -> System.out.println("  - " + c.getNombre()));
            System.out.println("Categorías de GASTO:");
            gestorCategorias.obtenerCategoriasPorTipo(TipoCategoria.GASTO)
                    .forEach(c -> System.out.println("  - " + c.getNombre()));

            //  PRUEBA 3: GESTIÓN DE CUENTAS 
            System.out.println("\n3. CREANDO CUENTAS");
            gestorCuentas.crearCuentaParaUsuario(usuario1, "Cuenta Corriente");
            gestorCuentas.crearCuentaParaUsuario(usuario1, "Cuenta Ahorros");
            gestorCuentas.crearCuentaParaUsuario(usuario2, "Cuenta Principal");

            Cuenta cuentaCorriente = usuario1.getCuentas().get(0);
            Cuenta cuentaAhorros = usuario1.getCuentas().get(1);
            Cuenta cuentaPrincipal = usuario2.getCuentas().get(0);

            //  PRUEBA 4: TRANSACCIONES 
            System.out.println("\n4. REGISTRANDO TRANSACCIONES");
            Categoria salario = gestorCategorias.buscarCategoria("Salario");
            Categoria comida = gestorCategorias.buscarCategoria("Comida");
            Categoria transporte = gestorCategorias.buscarCategoria("Transporte");
            Categoria inversiones = gestorCategorias.buscarCategoria("Inversiones");

            gestorTransacciones.registrarIngreso(cuentaCorriente, new BigDecimal("3000"), "Salario enero", salario);
            gestorTransacciones.registrarIngreso(cuentaAhorros, new BigDecimal("500"), "Rendimientos", inversiones);
            gestorTransacciones.registrarIngreso(cuentaPrincipal, new BigDecimal("2500"), "Salario enero", salario);

            gestorTransacciones.registrarGasto(cuentaCorriente, new BigDecimal("150"), "Supermercado", comida);
            gestorTransacciones.registrarGasto(cuentaCorriente, new BigDecimal("50"), "Gasolina", transporte);
            gestorTransacciones.registrarGasto(cuentaPrincipal, new BigDecimal("200"), "Restaurante", comida);

            //  PRUEBA 5: CÁLCULOS DE BALANCES 
            System.out.println("\n5. CALCULANDO BALANCES");
            System.out.println("* Cuenta Corriente: $" + calculadora.calcularBalanceCuenta(cuentaCorriente));
            System.out.println("* Cuenta Ahorros: $" + calculadora.calcularBalanceCuenta(cuentaAhorros));
            System.out.println("* Cuenta Principal: $" + calculadora.calcularBalanceCuenta(cuentaPrincipal));

            System.out.println("* Total usuario1: $" + calculadora.calcularBalanceTotalUsuario(usuario1));
            System.out.println("* Total usuario2: $" + calculadora.calcularBalanceTotalUsuario(usuario2));

            System.out.println("* Balance comida (usuario1): $" + calculadora.calcularBalancePorCategoria(usuario1, "Comida"));
            System.out.println("* Balance salario (usuario1): $" + calculadora.calcularBalancePorCategoria(usuario1, "Salario"));

            //  PRUEBA 6: HISTORIAL DE TRANSACCIONES 
            System.out.println("\n6. HISTORIAL DE TRANSACCIONES");
            cuentaCorriente.getTransacciones().forEach(t -> {
                String tipo = t instanceof Ingreso ? "INGRESO" : "GASTO";
                System.out.printf("  - %s: $%s | %s (%s)%n", tipo, t.getMonto(), t.getDescripcion(), t.getCategoria().getNombre());
            });

            // Ordenadas por fecha ascendente
            System.out.println("\n6.1. HISTORIAL DE TRANSACCIONES");
            cuentaCorriente.getTransaccionesOrdenadasAsc().forEach(t -> {
                String tipo = t instanceof Ingreso ? "INGRESO" : "GASTO";
                System.out.printf("  - %s: $%s | %s (%s)%n", tipo, t.getMonto(), t.getDescripcion(), t.getCategoria().getNombre());
            });
            // Ordenadas por fecha descendente
            System.out.println("\n6.1. HISTORIAL DE TRANSACCIONES");
            cuentaCorriente.getTransaccionesOrdenadasDesc().forEach(t -> {
                String tipo = t instanceof Ingreso ? "INGRESO" : "GASTO";
                System.out.printf("  - %s: $%s | %s (%s)%n", tipo, t.getMonto(), t.getDescripcion(), t.getCategoria().getNombre());
            });

            //  PRUEBA 7: OPERACIONES AVANZADAS
            System.out.println("\n7. OPERACIONES AVANZADAS");

            // Editar transacción: cambiar monto y descripción
            Transaccion transaccionEditar = cuentaCorriente.getTransacciones().get(1); // gasolina
            gestorTransacciones.editarTransaccion(cuentaCorriente, transaccionEditar.getId(), new BigDecimal("80"), "Gasolina premium");

            System.out.println("* Transacción editada: " + transaccionEditar.getDescripcion() + " $" + transaccionEditar.getMonto());

            // Recalcular balance después de edición
            System.out.println("* Nuevo saldo cuenta Corriente: $" + calculadora.calcularBalanceCuenta(cuentaCorriente));

            // Eliminar cuenta
            gestorCuentas.eliminarCuenta(usuario1, cuentaAhorros.getId());
            System.out.println("* Cuenta Ahorros eliminada. Cuentas restantes: " + usuario1.getCuentas().size());

            // Eliminar usuario
            gestorUsuarios.eliminarUsuario(usuario2.getId());
            System.out.println("* Usuario 2 eliminado correctamente.");

        } catch (Exception e) {
            System.err.println("\n[ERROR] " + e.getMessage());
        }

        System.out.println("\n FIN DE LA PRUEBA ");
    }
}
