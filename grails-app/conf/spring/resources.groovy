import stock.locator.UserPasswordEncoderListener
import stock.locator.converter.ExcelToCsvConverter

// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener, ref('hibernateDatastore'))
}
