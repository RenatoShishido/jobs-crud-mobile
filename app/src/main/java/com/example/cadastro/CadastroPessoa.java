package com.example.cadastro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cadastro.config.DatabaseHelper;
import com.example.cadastro.dao.EmpregoDAO;
import com.example.cadastro.dao.PessoaDAO;
import com.example.cadastro.entities.Emprego;
import com.example.cadastro.entities.Pessoa;
import com.example.cadastro.util.ArrayAdapterCustom;
import com.example.cadastro.util.UtilAlert;

import java.util.ArrayList;

public class CadastroPessoa extends AppCompatActivity {
    private EditText edtNome, edtEmail, edtCpf, edtTelefone;
    private Button btnVariavel;
    private Spinner spnSelectVagas;
    private ArrayList arrayListEmprego;
    private ArrayAdapterCustom<Emprego> vagasAdapter;
    Pessoa pessoa, altPessoa;
    private int  vagaId;
    DatabaseHelper databaseHelper;
    PessoaDAO pessoaDAO;
    EmpregoDAO empregoDAO;
    long retornoBD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pessoa);
        Intent it=getIntent();

        pessoaDAO = new PessoaDAO(CadastroPessoa.this);
        empregoDAO = new EmpregoDAO(this);
        altPessoa = (Pessoa) it.getSerializableExtra("chave");
        pessoa = new Pessoa();
        databaseHelper = new DatabaseHelper(CadastroPessoa.this);
        edtNome = findViewById(R.id.edtDescricao);
        edtEmail = findViewById(R.id.edtHoras);
        edtCpf = findViewById(R.id.edtValor);
        edtTelefone = findViewById(R.id.edtTelefone);
        btnVariavel = findViewById(R.id.btnVariavel);

        spnSelectVagas = findViewById(R.id.spnSelectVagas);


        preencheLista();

        spnSelectVagas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Emprego emprego = (Emprego) parent.getSelectedItem();
                vagaId = emprego.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        if(altPessoa != null){
            btnVariavel.setText("ALTERAR");
            edtNome.setText(altPessoa.getNome());
            edtEmail.setText(altPessoa.getEmail()+"");
            edtCpf.setText(altPessoa.getCpf());
            edtTelefone.setText(altPessoa.getTelefone());
            pessoa.setId(altPessoa.getId());
            Emprego emprego = altPessoa.getEmprego();
            setItemSpinner(emprego);
        }
        else{
            btnVariavel.setText("SALVAR");
        }
        btnVariavel.setOnClickListener(v -> {

                String nome = edtNome.getText().toString();
                String email = edtEmail.getText().toString();
                String cpf = edtCpf.getText().toString();
                String telefone = edtTelefone.getText().toString();
                long retornoBD;
                pessoa.setVagaId(vagaId != 0 ? vagaId : -1);
                pessoa.setNome(nome);
                pessoa.setEmail(email);
                pessoa.setCpf(cpf);
                pessoa.setTelefone(telefone);
                if (btnVariavel.getText().toString().equals("SALVAR")) {
                    try {
                        retornoBD = pessoaDAO.insert(pessoa);
                        if (retornoBD == -1) {
                            UtilAlert.alert(CadastroPessoa.this, "Erro ao Cadastrar!");
                        } else {
                            UtilAlert.alert(CadastroPessoa.this, "Cadastro realizado com sucesso!");
                        }
                    }
                    catch(IllegalArgumentException ex) {
                        UtilAlert.alert(this, ex.getMessage());
                    }

                } else {
                    pessoaDAO.update(pessoa);
                    databaseHelper.close();
                }
                finish();


        });
    }
    public void preencheLista(){

        databaseHelper = new DatabaseHelper(this);
        arrayListEmprego = empregoDAO.selectAll();
        databaseHelper.close();

         vagasAdapter  =new ArrayAdapterCustom<Emprego>(this,
                android.R.layout.simple_spinner_item, arrayListEmprego);
        vagasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSelectVagas.setAdapter(vagasAdapter);

    }
    public void setItemSpinner(Emprego emprego) {
        int posicaoArray = 0;
        boolean fim = false;
    if(arrayListEmprego != null) {
        for (int i = 0; (i <= arrayListEmprego.size() - 1 && !fim); i++) {

            if (((Emprego) arrayListEmprego.get(i)).getId() == emprego.getId()) {

                posicaoArray = i;
                fim = true;
            } else {
                posicaoArray = 0;
            }
        }
        spnSelectVagas.setSelection(posicaoArray);
    }
    }

}