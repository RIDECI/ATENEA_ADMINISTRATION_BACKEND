package edu.dosw.rideci.domain.model;

import java.util.Arrays;
import java.util.Objects;

/**
 * Contenedor inmutable para resultado de exportación.
 * hace copia defensiva del arreglo de bytes en constructor.
 */
public final class ExportedReport {
    private final byte[] content;
    private final String filename;
    private final String mediaType;

    /**
     * Record que representa un reporte exportado con su contenido y metadatos
     *
     * @param content Bytes del contenido del archivo
     * @param filename Nombre del archivo generado
     * @param mediaType Tipo MIME del archivo
     */
    public ExportedReport(byte[] content, String filename, String mediaType) {
        this.content = content == null ? new byte[0] : Arrays.copyOf(content, content.length);
        this.filename = filename;
        this.mediaType = mediaType;
    }

    /**
     * Retorna una copia segura del contenido del archivo
     *
     * @return Copia del array de bytes del contenido
     */
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }

    /**
     * Retorna el nombre del archivo generado
     *
     * @return Nombre del archivo
     */
    public String filename() {
        return filename;
    }

    /**
     * Retorna el tipo MIME del archivo
     *
     * @return Tipo MIME como "application/pdf", "text/csv"
     */
    public String mediaType() {
        return mediaType;
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
        if (!(o instanceof ExportedReport)) return false;
        ExportedReport that = (ExportedReport) o;
        return Arrays.equals(content, that.content)
                && Objects.equals(filename, that.filename)
                && Objects.equals(mediaType, that.mediaType);
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
