package stock.locator.converter

/**
 * A simple file converter
 *
 * @author Valentin Ponochevniy
 */
interface Converter {

    /**
     * Converts target file to another format
     * @param file
     * @return a converted file
     *
     * @author Valentin Ponochevniy
     */
    File convert(File file)

}