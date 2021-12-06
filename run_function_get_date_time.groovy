import java.text.SimpleDateFormat

def dateFormat = new SimpleDateFormat("yyMMdd_HHmmss")
def date = new Date()

return dateFormat.format(date)