
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Stack;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PoozInterprteter
{
	private Object[] consts;
	private int const_count = 0;
	
	private PoozPooZ pooz_instance = new PoozPooZ();
	
	private HashMap<Long, PoozSingleLine> all_line = new HashMap<Long, PoozSingleLine>();
	
	private long line_no;
	
	private ArrayList<PoozFrame> frame_stack = new ArrayList<PoozFrame>();
	
	public void runFromFile(File f) throws FileNotFoundException, IOException{
		BufferedReader br = new BufferedReader(
			new InputStreamReader(
				new FileInputStream(f)));
				
		String oline;
		
		while((oline = br.readLine()) != null){
			PoozSingleLine psl = new PoozSingleLine(oline);
			
			all_line.put(psl.line_no, psl);
		}
		
		runAllLine();
	}
	
	private void runAllLine(){
		while(line_no < all_line.keySet().size()){
			
			if (all_line.containsKey(line_no)){
				execop(all_line.get(line_no));
			}
			
			line_no++;
		}
	}
	
	public void execop(PoozSingleLine line){
		
		switch(line.op){
			case "set_const_size":
				consts = new Object[(int)toNumber(line.args, "int")];
				break;
			
			case "const_int":
				pushConst(toNumber(line.args, "int"));
				break;
				
			case "const_str":
				pushConst(line.args);
				break;
				
			case "const_float":
				pushConst(toNumber(line.args, "float"));
				break;
				
			case "const_double":
				pushConst(toNumber(line.args, "double"));
				break;
				
			case "store_local":
				topFrame().local_vars.put(
					(String)consts[(int)toNumber(line.args, "int")],
					topFrame().stack.pop());
					
				break;
				
			case "load_local":
				topFrame().stack.push(
					topFrame().local_vars.get(
						(String)consts[(int)toNumber(line.args, "int")]));
				
				break;
				
			case "store_global":
				globalFrame().local_vars.put(
					(String)consts[(int)toNumber(line.args, "int")],
					globalFrame().stack.pop());
					
				break;
			
			case "load_global":
				globalFrame().stack.push(
					globalFrame().local_vars.get(
						(String)consts[(int)toNumber(line.args, "int")]));

				break;
				
			case "if_else_jump":
				boolean cmp = 
					cmpStack();
					
				if(cmp){
					jump(toNumber(line.args, "int"));
				}
				
				break;
				
			case "jump_absolute":
				jump(toNumber(line.args, "int"));
				
				break;
				
			case "add_int":
				topFrame().stack.push(addMuitInt(0));
				
				break;
				
			case "muit_int":
				topFrame().stack.push(addMuitInt(1));
				
				break;
				
			case "add_double":
				topFrame().stack.push(addMuitDouble(0));

				break;

			case "muit_double":
				topFrame().stack.push(addMuitDouble(1));

				break;
				
			case "add_float":
				topFrame().stack.push(addMuitFloat(0));

				break;

			case "muit_float":
				topFrame().stack.push(addMuitFloat(1));

				break;
				
			case "add_byte":
				topFrame().stack.push(addMuitByte(0));

				break;

			case "muit_byte":
				topFrame().stack.push(addMuitByte(1));

				break;
			
			case "sub_int":
				topFrame().stack.push(addMuitInt(2));

				break;
				
			case "div_int":
				topFrame().stack.push(addMuitInt(3));

				break;
				
			case "sub_double":
				topFrame().stack.push(addMuitDouble(2));

				break;

			case "div_double":
				topFrame().stack.push(addMuitDouble(3));

				break;
				
			case "sub_float":
				topFrame().stack.push(addMuitFloat(2));

				break;

			case "div_float":
				topFrame().stack.push(addMuitFloat(3));

				break;
				
			case "sub_byte":
				topFrame().stack.push(addMuitByte(2));

				break;

			case "div_byte":
				topFrame().stack.push(addMuitByte(3));

				break;
				
			case "sup_int":
				topFrame().stack.push(addMuitInt(4));
				
				break;
			
			case "sup_float":
				topFrame().stack.push(addMuitFloat(4));

				break;
				
			case "sup_double":
				topFrame().stack.push(addMuitDouble(4));

				break;
				
			case "sup_byte":
				topFrame().stack.push(addMuitByte(4));

				break;
				
			case "comp_int":
				comInt(toNumber(line.args, "int"));
				
				break;
				
			case "comp_double":
				comDouble(toNumber(line.args, "int"));

				break;
				
			case "comp_float":
				comFloat(toNumber(line.args, "int"));

				break;
				
			case "comp_byte":
				comByte(toNumber(line.args, "int"));

				break;
				
			case "print_stack_trace":
				printStackTrace();
				
				break;
				
			default:
				causeError("Unknown instruction :"+line.toString());
		}
	}
	
	private void printStackTrace(){
		for (PoozFrame pf : frame_stack){
			pooz_instance.Pz_PrintValue(pf.func_master);
		}
	}
	
	private void comInt(int mode){
		int a, b;
		
		b = topFrame().stack.pop();
		a = topFrame().stack.pop();
		
		switch(mode){
			case 0: /*>*/
				topFrame().stack.push(
					(a > b) ? 1 : 0);
				
				break;
				
			case 1: /*<*/
				topFrame().stack.push(
					(a < b) ? 1 : 0);

				break;
			
			case 2: /*==*/
				topFrame().stack.push(
					(a == b) ? 1 : 0);

				break;
			
			case 3: /*>=*/
				topFrame().stack.push(
					(a >= b) ? 1 : 0);

				break;
			
			case 4: /*<=*/
				topFrame().stack.push(
					(a <= b) ? 1 : 0);

				break;
			
			case 5: /*!=*/
				topFrame().stack.push(
					(a != b) ? 1 : 0);

				break;
			
			default:
				causeError("Unknown compare mode");
		}
	}
	
	private void comDouble(int mode){
		double a, b;

		b = topFrame().stack.pop();
		a = topFrame().stack.pop();

		switch(mode){
			case 0: /*>*/
				topFrame().stack.push(
					(a > b) ? 1 : 0);

				break;

			case 1: /*<*/
				topFrame().stack.push(
					(a < b) ? 1 : 0);

				break;

			case 2: /*==*/
				topFrame().stack.push(
					(a == b) ? 1 : 0);

				break;

			case 3: /*>=*/
				topFrame().stack.push(
					(a >= b) ? 1 : 0);

				break;

			case 4: /*<=*/
				topFrame().stack.push(
					(a <= b) ? 1 : 0);

				break;

			case 5: /*!=*/
				topFrame().stack.push(
					(a != b) ? 1 : 0);

				break;

			default:
				causeError("Unknown compare mode");
		}
	}
	
	private void comFloat(int mode){
		float a, b;

		b = topFrame().stack.pop();
		a = topFrame().stack.pop();

		switch(mode){
			case 0: /*>*/
				topFrame().stack.push(
					(a > b) ? 1 : 0);

				break;

			case 1: /*<*/
				topFrame().stack.push(
					(a < b) ? 1 : 0);

				break;

			case 2: /*==*/
				topFrame().stack.push(
					(a == b) ? 1 : 0);

				break;

			case 3: /*>=*/
				topFrame().stack.push(
					(a >= b) ? 1 : 0);

				break;

			case 4: /*<=*/
				topFrame().stack.push(
					(a <= b) ? 1 : 0);

				break;

			case 5: /*!=*/
				topFrame().stack.push(
					(a != b) ? 1 : 0);

				break;

			default:
				causeError("Unknown compare mode");
		}
	}
	
	private void comByte(int mode){
		byte a, b;

		b = topFrame().stack.pop();
		a = topFrame().stack.pop();

		switch(mode){
			case 0: /*>*/
				topFrame().stack.push(
					(a > b) ? 1 : 0);

				break;

			case 1: /*<*/
				topFrame().stack.push(
					(a < b) ? 1 : 0);

				break;

			case 2: /*==*/
				topFrame().stack.push(
					(a == b) ? 1 : 0);

				break;

			case 3: /*>=*/
				topFrame().stack.push(
					(a >= b) ? 1 : 0);

				break;

			case 4: /*<=*/
				topFrame().stack.push(
					(a <= b) ? 1 : 0);

				break;

			case 5: /*!=*/
				topFrame().stack.push(
					(a != b) ? 1 : 0);

				break;

			default:
				causeError("Unknown compare mode");
		}
	}
	
	private int addMuitInt(int mode){
		int a, b;
		
		a = topFrame().stack.pop();
		b = topFrame().stack.pop();
		
		if(!(a instanceof int) || !(b instanceof int)){
			causeError("Is not int");
		}
		
		switch(mode){
			case 0: /*add*/
				return a + b;
			
			case 1: /*muit*/
				return a * b;
				
			case 2: /*sub*/
				return b - a;
				
			case 3: /*div*/
				return b - a;
				
			case 4: /*surplus*/
				return b % a;
				
				
			default:
				causeError("Unknown operate mode!");
		}
		
		return 0;
	}
	
	private float addMuitFloat(int mode){
		float a, b;

		a = topFrame().stack.pop();
		b = topFrame().stack.pop();
		
		if(!(a instanceof float) || !(b instanceof float)){
			causeError("Is not float");
		}
		
		switch(mode){
			case 0: /*add*/
				return a + b;

			case 1: /*muit*/
				return a * b;
				
			case 2: /*sub*/
				return b - a;

			case 3: /*div*/
				return b - a;
				
			case 4: /*surplus*/
				return b % a;
				

			default:
				causeError("Unknown operate mode!");
		}

		return 0;
	}
	
	private double addMuitDouble(int mode){
		double a, b;

		a = topFrame().stack.pop();
		b = topFrame().stack.pop();
		
		if(!(a instanceof double) || !(b instanceof double)){
			causeError("Is not double");
		}

		switch(mode){
			case 0: /*add*/
				return a + b;

			case 1 /*muit*/:
				return a * b;
				
			case 2: /*sub*/
				return b - a;

			case 3: /*div*/
				return b - a;
				
			case 4: /*surplus*/
				return b % a;
				

			default:
				causeError("Unknown operate mode!");
		}

		return 0;
	}
	
	private byte addMuitByte(int mode){
		byte a, b;

		a = topFrame().stack.pop();
		b = topFrame().stack.pop();
		
		if(!(a instanceof byte) || !(b instanceof byte)){
			causeError("Is not byte");
		}

		switch(mode){
			case 0: /*add*/
				return (byte)(a + b);

			case 1:
				return (byte)(a * b);
				
			case 2: /*sub*/
				return (byte)(b - a);

			case 3: /*div*/
				return (byte)(b - a);
				
			case 4: /*surplus*/
				return (byte)(b % a);
				

			default:
				causeError("Unknown operate mode!");
		}

		return 0;
	}
	
	private void jump(long line_no){
		this.line_no = line_no;
	}
	
	private boolean cmpStack(){
		try{
			return !(topFrame().stack.pop() == 0);
		}catch(NumberFormatException e){
			causeError("Not a boolean value");
		}
		
		return false;
	}
	
	private void causeError(String info){
		PoozError.cause("Interprteter Error", info);
	}
	
	private PoozFrame globalFrame(){
		return frame_stack.get(0);
	}
	
	private PoozFrame topFrame(){
		return frame_stack.get(frame_stack.size() - 1);
	}
	
	private void pushFrame(PoozFrame pf){
		frame_stack.add(pf);
	}
	
	private void vminit(){
		frame_stack.add(new PoozFrame("__main__"));
	}
	
	private void pushConst(Object args){
		consts[const_count++] = args;
	}
	
	private Object toNumber(String sval, String type){
		try{
			switch(type){
				case "int":
					return Integer.parseInt(sval);
					
				case "float":
					return Float.parseFloat(sval);
					
				case "double":
					return Double.parseDouble(sval);
					
				case "short":
					return Short.parseShort(sval);
					
				default:
					PoozError.cause("Interprteter Error", "unknown switch type '"+type+"'");
			}
		}catch(NumberFormatException e){
			PoozError.cause("Interprteter Error", "'"+sval+"' to "+type+" fail");
		}
		
		return null;
	}
}

class PoozMethod{
	PoozSingleLine[] lines;
}

class PoozFrame{
	HashMap<String, Object> local_vars = new HashMap<String, Object>();
	Stack<Object> stack = new Stack<Object>();
	
	String func_master;
	
	PoozFrame(String func_master){
		this.func_master = func_master;
	}
}

class PoozSingleLine{
	long line_no;
	String op;
	String args;
	
	String origin_line;
	
	PoozSingleLine(String line){
		/*
		 * [lineno] [instr] [args]
		 **/
		 
		 origin_line = line;
		 parseLine(line);
	}
	
	private void parseLine(String line){
		String[] part = line.split("\\s");
		
		try{
			
			line_no = Integer.parseInt(part[0]);
			op = part[1];
			args = part[2];
			
		}catch(NumberFormatException e){
			PoozError.cause("Interprteter Error", line);
		}
	}

	@Override
	public String toString()
	{
		return origin_line;
	}
	
}
