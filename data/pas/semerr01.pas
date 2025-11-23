program testAdv;
var i: integer;
    a: array[1..10] of integer;
    msg: char;
    debug: boolean;
    i: integer;  { <<<< sematic error here }
procedure printData;
var  i: integer;
begin
    i := 1;
    while i <= 10 do
    begin
        write('[', msg, '] i=', a[i]);
        i := i + 1;
    end;
end;

{ comment test here        }
{ begin for if end write }
{ i=1; i='a'; ..., ., : ;* }

begin
    debug := true;
    msg := 'D';
    write('***** calculating ******');
    i := 1;
    while i <= 10 do
    begin
        a[i] := i * i;
        if debug = true then begin write(i); end;
        i := i + 1;
    end;
    write('***** result ******');
    printData;
end.
