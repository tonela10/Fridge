# Cachos Fridge
App offline para gestionar inventario, deudas, bote comun y fondos personales en una nevera compartida.
## 1) Objetivo funcional
- Flujo principal: `Home -> SelectBuyer -> Payment`.
- Desde Home se abre `Menu Rail` para navegar a:
  - `Deudas`
  - `Inventario` (editar stock, agregar productos, eliminar si no tienen asset)
  - `Anadir Bote` (dialogo)
  - `Anadir Fondos` (dialogo)
- Reglas de negocio:
  - Balance negativo: la persona debe dinero.
  - Balance positivo: la persona tiene credito.
  - `PAGAR DE BOTE` deshabilitado (gris) cuando no hay bote suficiente.
  - **Productos con asset (imagen) NO se pueden eliminar**.
  - Gestión de inventario: agregar productos nuevos, modificar precio, aumentar/disminuir stock.
## 2) Arquitectura
- **UI**: Jetpack Compose.
- **Patron**: MVVM.
- **Persistencia local**: Room.
- **DI actual**: AppContainer manual (sustituible por Hilt cuando la combinacion AGP/Hilt del proyecto lo permita).
- **Navegacion actual**: Compose Navigation (estructura preparada para migrar a navigation3 tipado).
### Estructura de paquetes
- `app/src/main/java/com/sedilant/cachosfridge/data`
  - Entidades Room (`ProductEntity`, `PersonEntity`, `BoteEntity`)
  - DAOs
  - `FridgeDatabase`
  - `FridgeRepository`
  - Seed inicial
  - AppContainer DI manual
- `app/src/main/java/com/sedilant/cachosfridge/ui`
  - Un archivo `Screen` y uno `ViewModel` por pantalla
  - Formateador monetario `es-ES`
## 3) Seed inicial (primera ejecucion)
Se inserta automaticamente si no hay productos:
### Productos (precios desde Home + Cerveza 0,50 EUR)
Todos tienen `hasAsset=true`, lo que impide su eliminación:
- Pepsi: 0,50 EUR (con asset)
- Kas Naranja: 0,50 EUR (con asset)
- Kas Limon: 0,50 EUR (con asset)
- Cerveza: 0,50 EUR (con asset)
- Agua: 0,20 EUR (con asset)
- Patatas: 0,50 EUR (con asset)
- Palomitas: 0,50 EUR (con asset)
### Stock inicial (desde inventario_screen)
- Pepsi: 12
- Kas Naranja: 8
- Kas Limon: 5
- Cerveza: 24
- Agua: 30
- Patatas: 15
- Palomitas: 10
### Personas y balances iniciales (desde deudas_screen_updated)
- Mario: -1,00 EUR
- Raul: +2,50 EUR
- Toño: +5,75 EUR
- Paton: -3,20 EUR
- Canut: +8,40 EUR
### Bote inicial
- 0,00 EUR
## 4) Formato monetario
Todos los importes se muestran en `es-ES` mediante `NumberFormat.getCurrencyInstance(Locale("es", "ES"))`.
## 5) Pantallas implementadas
- `HomeScreen` + `HomeViewModel`
- `SelectBuyerScreen` + `SelectBuyerViewModel`
- `PaymentScreen` + `PaymentViewModel`
- `DebtsScreen` + `DebtsViewModel`
- `InventoryScreen` + `InventoryViewModel` (con agregar/editar/eliminar)
- `AddBoteDialogScreen` + `AddBoteViewModel`
- `AddFundsDialogScreen` + `AddFundsViewModel`
- `MenuRailScreen` (componente de navegacion lateral)
## 6) Reglas de pago
Al comprar un producto:
1. Se reduce stock en 1.
2. Segun metodo:
   - `PAGAR YA`: no modifica balance de persona.
   - `ANADIR A LA CUENTA`: balance persona -= precio.
   - `PAGAR DE BOTE`: bote -= precio (solo si bote >= precio).
## 7) Gestión de inventario
- **Agregar producto**: formulario con nombre, precio (EUR), stock.
- **Editar stock**: botones +1/-1 por producto.
- **Editar precio**: actualmente disponible via ViewModel (estructura lista para UI).
- **Eliminar producto**: solo si `hasAsset=false`; productos seed con imágenes no se pueden borrar.
## 8) Recursos y localizacion
- Todas las cadenas van en `app/src/main/res/values/strings.xml`.
- Evitar hardcoded strings en composables.
## 9) Pasos para ejecutar
```bash
cd /Users/sedilant/Projects/CachosFridge
./gradlew test
./gradlew :app:assembleDebug
```
## 10) Siguientes mejoras sugeridas
- Migrar DI manual -> Hilt en cuanto la version de plugin sea compatible con AGP del proyecto.
- Migrar la navegacion actual a API final de `androidx.navigation3` cuando se cierre la version objetivo en el proyecto.
- Sustituir selector de persona de `AddFunds` por dropdown Compose.
- Anadir tests de repositorio para reglas de pago y seed.
- Registrar historial de transacciones (tabla Room de movimientos).
- Mejorar UI visual de Inventario para editar precio inline.
