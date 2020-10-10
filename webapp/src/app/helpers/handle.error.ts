import { ToastrService } from 'ngx-toastr';
import { Observable, of } from 'rxjs';

/**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   * @param show - is it visible
   * @param message - error message
   */
export function handleError<T>(
    service : ToastrService = this.toastrService,
    operation = 'operation',
    result?: T,
    show: boolean = true,
    message: string = 'An error occurred while performing'
  ) {
    return (error: any): Observable<T> => {
      if(show) {
        if(error.error instanceof Blob && error.status < 500) {
          var myReader = new FileReader();
          myReader.onload = function(){
            const msg = JSON.parse(myReader.result.toString()).message;
            service.error(msg, 'Error 😅');
            console.log(msg);
          };
          myReader.readAsText(error.error);
        } else {
          const why = `${error.error.message ? error.error.message : message} ${operation}: ${error.statusText}`;
          service.error(why, 'Error 😅');
          console.log(why);
        }
      }
      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }