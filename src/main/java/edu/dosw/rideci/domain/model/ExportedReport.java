package edu.dosw.rideci.domain.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Contenedor inmutable para resultado de exportación.
 * hace copia defensiva del arreglo de bytes en constructor.
 */
public record ExportedReport(byte[] content, String filename, String mediaType) {

    /**
     * Realiza copia defensiva del array de bytes.
     */
    public ExportedReport {
        content = content == null ? new byte[0] : content.clone();
    }

    /**
     * Retorna una copia segura del contenido del archivo
     *
     * @return Copia del array de bytes del contenido
     */
    @Override
    public byte[] content() {
        return content.clone();
    }

    /**
     * Compara esta instancia con otro objeto para igualdad
     *
     * @param o Objeto a comparar
     * @return true si los objetos son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExportedReport other)) return false;
        return Arrays.equals(this.content, other.content)
                && Objects.equals(this.filename, other.filename)
                && Objects.equals(this.mediaType, other.mediaType);
    }

    /**
     * Retorna el código hash de esta instancia
     *
     * @return Código hash calculado basado en contenido, filename y mediaType
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(filename, mediaType);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    /**
     * Retorna representación en string de esta instancia
     *
     * @return String con información del reporte exportado (excluye el contenido completo por seguridad)
     */
    @Override
    public String toString() {
        return "ExportedReport{contentLength=" + (content == null ? 0 : content.length) +
                ", filename='" + filename + '\'' +
                ", mediaType='" + mediaType + '\'' +
                '}';
    }
}
