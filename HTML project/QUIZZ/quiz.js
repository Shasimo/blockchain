
let questions = [
    {
        id: 1,
        question: "C'est quoi une blockchain?",
        answer:"Une technologie de stockage et de transmission d'informations décentralisée",
        options: [
            "Une tour dans minecraft",
            "Une technologie de stockage et de transmission d'informations décentralisée",
            "Une banque traditionnelle comme ING,BELFIUS,autre",
            "None of these"
        ]   
    },
	{
		id: 2,
		question: "Pourquoi la blockchain peut etre utilisée?",
        answer:"Pour la sécurité dans les transactions en ligne",
        options: [
            "Pour assurer une bonne vitesse de transmission de données",
            "Pour s'échapper d'une crevasse dans minecraft",
            "Pour stocker plusieurs données dans un endroit",
            "Pour la sécurité dans les transactions en ligne"
        ] 
	},
	{
		id: 3,
		question: "Un bloc est lié au bloc précèdent ou au bloc suivant?",
		answer:"au bloc précèdent",
		options: [
			"au bloc précèdent",
			"au bloc suivant",
			"aucun réponse n'est pas correcte",
			"il y a pas de blocs"
			
		]
	},
	{
		id: 4,
		question: "C'est quoi l'identifiant d'un bloc",
		answer:"hash",
		options: [
			"timestamp(l'heure de la création du bloc)",
			"les transactions",
			"hash",
			"aucun réponse correcte"
			
		]
	},
	{
		id: 5,
		question: "A quoi sert les clefs RSA",
		answer:"chiffrement",
		options: [
			"hasher",
			"déverouiller la porte",
			"chiffrement",
			"verouiller la porte"
			
		]
	},
	{
		id: 6,
		question: "A quoi sert le SHA-256",
		answer:"hasher",
		options: [
			"miauler",
			"Hash-taguer les données",
			"hasher",
			"aucun réponse correcte"
			
		]
	},
	{
		id: 7,
		question: "Dans un réseaux P2P le client communique avec qui?",
		answer:"un autre client",
		options: [
			"lui-meme",
			"le serveur",
			"un autre client",
			"personne"
			
		]
	},
	
];

let question_count = 0;
let points = 0;


window.onload = function(){
    show(question_count);
};

function show(count){
    let question = document.getElementById("questions");
    let[first, second, third, fourth] = questions[count].options;

    question.innerHTML = `<h2>Q${count + 1}. ${questions[count].question}</h2>
    <ul class="option_group">
    <li class="option">${first}</li>
    <li class="option">${second}</li>
    <li class="option">${third}</li>
    <li class="option">${fourth}</li>
    </ul>`;
    toggleActive();  
}

function toggleActive(){
    let option = document.querySelectorAll("li.option");
    for(let i=0; i < option.length; i++){
        option[i].onclick = function(){
            for(let i=0; i < option.length; i++){
                if(option[i].classList.contains("active")){
                    option[i].classList.remove("active");
                }
            }
            option[i].classList.add("active");
        }
    }
}

function next(){

    if(question_count == questions.length -1){
        location.href = "final.html";
    }
    console.log(question_count);


let user_answer = document.querySelector("li.option.active").innerHTML;

if(user_answer == questions[question_count].answer){
    points += 10;
    sessionStorage.setItem("points",points);
}
console.log(points);

question_count++;
show(question_count);
}