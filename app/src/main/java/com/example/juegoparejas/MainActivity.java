package com.example.juegoparejas;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

	Chronometer crono;
    TextView tvPuntos;
    Button btStart;
    Button btStop;
    TableLayout tl; // Es donde se juega
    LinearLayout ll; // Layout de inicio
    LinearLayout llJuego;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        
        // Guardamos accesos a los controles que vamos a usar para que se puedan modificar más facilmente
        crono=((Chronometer)findViewById(R.id.chrono));
        tvPuntos=(TextView)findViewById(R.id.tvPuntuacion);
        tl=(TableLayout)findViewById(R.id.tlJuego);
        llJuego=(LinearLayout)findViewById(R.id.llJuego);
        ll=(LinearLayout)findViewById(R.id.llComienzo);
        btStart=(Button)findViewById(R.id.btStart);
        btStop=(Button)findViewById(R.id.btStop);
		btStop.setVisibility(View.VISIBLE);
        
        res=getResources();
        
        initJuego();
    }
    
    int iPuntuacion=0;
    int [] rDibujos={R.drawable.conejo,R.drawable.oveja,R.drawable.pollo,R.drawable.rinoceronte,R.drawable.serpiente,R.drawable.tiburon};
    int [] rIVs={R.id.iv11,R.id.iv12,R.id.iv13,R.id.iv21,R.id.iv22,R.id.iv23,R.id.iv31,R.id.iv32,R.id.iv33,R.id.iv41,R.id.iv42,R.id.iv43};
    int [] cartas=new int[rIVs.length];
    int [] estadoCartas=new int[rIVs.length];
    
    int ciCartaAcertada=1;
    int ciCartaPendiente=0;
    
    void initJuego()
    {
    	// Inicializo todas las cartas a 0
    	for(int i=0;i<cartas.length;i++)
    	{
    		cartas[i]=0;
    		estadoCartas[i]=ciCartaPendiente;
    		((ImageView)findViewById(rIVs[i])).setImageResource(R.drawable.interrogacion);
    	}
    	// Relleno las cartas con el id de la imagen que van a contener
    	for(int i=0;i<rDibujos.length;i++)
   		{
    		int iRandomCarta;
    		
    		// Carta primera de la pareja
    		do
    		{
    			iRandomCarta=getRandomCarta();
    		}
    		while(cartas[iRandomCarta]!=0);
    		cartas[iRandomCarta]=rDibujos[i];
    		// Carta segunda de la pareja
    		do
    		{
    			iRandomCarta=getRandomCarta();
    		}
    		while(cartas[iRandomCarta]!=0);
    		cartas[iRandomCarta]=rDibujos[i];

   		}
    	
    	// Inicializamos la puntuación
    	iPuntuacion=0;
    	actualizaPuntuacion();
    	
    	btStop.setText(res.getString(R.string.stop));
    	
    	// Ponemos a 0 el temporizador y el mensaje de pulsar Start
    	stopGame(null);
    	

    }
    
    // Devuelve la posici�n en el array de ids del control con el ID que pasamos
    int getPosicion(int ID)
    {
    	for(int i=0;i<rIVs.length;i++)
    		if(ID==rIVs[i])
    			return i;
    	return -1;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
 
    int iCartaPrimera;
    int iCartaSegunda;
    boolean bPrimeraCarta=true;
    ImageView ivCartaPrimera;
    ImageView ivCartaSegunda;
    boolean bEsperandoVolteo=false;
    boolean bJugando=false;
    Resources res;
    void actualizaPuntuacion()
    {
    	
    	tvPuntos.setText(res.getString(R.string.puntuacion)+iPuntuacion);
    }
    
    public void pulsaImagen(View v)
    {
    	if(bJugando==false)
    		return;
    	
    	if(bEsperandoVolteo) // Han pulsado mientras esperamos para voltear, entonces volteamos
    	{
    		ivCartaPrimera.setImageResource(R.drawable.interrogacion);
    		ivCartaSegunda.setImageResource(R.drawable.interrogacion);
    		bEsperandoVolteo=false;
    	}

    	
    	// Busco que control han pulsado
    	int iPosicion=getPosicion(v.getId());
    	// Si ya la acerto no hago nada
    	if(estadoCartas[iPosicion]==ciCartaAcertada)
    		return;
    	
    	if(bPrimeraCarta)
    	{
    		// Volteo la carta 
    		ivCartaPrimera=((ImageView)v);
    		ivCartaPrimera.setImageResource(cartas[iPosicion]);
    		
    		//Guardo como primeraCarta
    		iCartaPrimera=iPosicion;
    		
    		//Pasamos a esperar la segunda
    		bPrimeraCarta=false;
    	}
    	else
    	{
    		// Volteo la carta 
    		ivCartaSegunda=((ImageView)v);
    		ivCartaSegunda.setImageResource(cartas[iPosicion]);

    		// Guardo la segunda carta
    		iCartaSegunda=iPosicion;
    		
    		
    		// Vemos si hay acierto o no
    		if(cartas[iCartaPrimera]==cartas[iCartaSegunda]) // Acierto
    		{
    			// Marcamos como acertadas las 2 cartas
    			estadoCartas[iCartaPrimera]=ciCartaAcertada;
    			estadoCartas[iCartaSegunda]=ciCartaAcertada;
    			
    			//Incrementamos la puntaci�n
    			iPuntuacion++;	
    			// Actualizamos el texto de la puntuacion
    			actualizaPuntuacion();
    			
    			if(iPuntuacion==rDibujos.length )
    			{
    				
    				btStop.setText("Volver a jugar");
    				crono.stop();
    			}
    		}
    		else  // Hemos fallado
    		{
    			//Lanzamos el trabajo de invertir las cartas dentro de 2seg
    			bEsperandoVolteo=true;
	        	Handler handler = new Handler(); 
	        	handler.postDelayed(new Runnable() { 
	               public void run() {
            	    //Si todavía es necesario invertiremos las cartas
	            	   if(bEsperandoVolteo)
	            	   {
	            		   ivCartaPrimera.setImageResource(R.drawable.interrogacion);
	            		   ivCartaSegunda.setImageResource(R.drawable.interrogacion);
                    
	            		   bEsperandoVolteo=false;
	            	   }
                   }
	        	}, 2000); 
    		}
    		bPrimeraCarta=true;
    	}

    	
    }
    int getRandomCarta()
    {
    	return(int)(Math.random()*cartas.length);
    }
    
    
    public void startGame(View v)
    {
    	crono.setBase(SystemClock.elapsedRealtime());
    	initJuego();
    	crono.start();
    	llJuego.setVisibility(View.VISIBLE);
    	ll.setVisibility(View.INVISIBLE);

    	bJugando=true;
    }
    
    public void stopGame(View v)
    {
    	if(iPuntuacion==rDibujos.length)
    	{
    		initJuego();
    		return;
    	}
    	crono.stop();

    	bJugando=false;
    	
    	llJuego.setVisibility(View.INVISIBLE);
    	ll.setVisibility(View.VISIBLE);

    	long elapsedMillis = SystemClock.elapsedRealtime() - crono.getBase();
    }
    
}
