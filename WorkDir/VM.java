import java.io.Serializable; 
public class VM implements Serializable{
   private static final long serialVersionUID = -888833558023990884L;
   private String nombre;
   private int vcpus;

   public VM(String nombre, int vcpus) {
      this.nombre = nombre;
      this.vcpus = vcpus;
   }

   public String getNombre() {
      return this.nombre;
   }

   public void setNombre(String nombre) {
      this.nombre = nombre;
   }

   public int getVcpus() {
      return this.vcpus;
   }

   public void setVcpus(int vcpus) {
      this.vcpus = vcpus;
   }

   public VM nombre(String nombre) {
      this.nombre = nombre;
      return this;
   }

   public VM vcpus(int vcpus) {
      this.vcpus = vcpus;
      return this;
   }

   @Override
   public String toString() {
      return "{" +
         " nombre='" + getNombre() + "'" +
         ", vcpus='" + getVcpus() + "'" +
         "}";
   }
}
